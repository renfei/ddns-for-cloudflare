package net.renfei.ddns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import net.renfei.cloudflare.Cloudflare;
import net.renfei.cloudflare.entity.CreateDnsRecord;
import net.renfei.cloudflare.entity.DnsRecords;
import net.renfei.cloudflare.entity.Zone;
import net.renfei.sdk.http.HttpRequest;
import net.renfei.sdk.http.HttpResult;
import net.renfei.sdk.utils.BeanUtils;
import net.renfei.sdk.utils.DateUtils;
import net.renfei.sdk.utils.HttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>Title: Main</p>
 * <p>Description: </p>
 *
 * @author RenFei(i @ renfei.net)
 */
public class Main {
    @Parameter(names = {"--zone", "-z"})
    private String zoneName;
    @Parameter(names = {"--token", "-t"})
    private String token;
    @Parameter(names = {"--domain", "-d"})
    private String domain;
    private Zone zone;
    private String myIp;
    private Cloudflare cloudflare;
    private boolean deployed = false;

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.scheduleJob();
    }

    private void scheduleJob() {
        cloudflare = new Cloudflare(token);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleWithFixedDelay(() -> {
            System.out.println("==== DDNS-for-Cloudflare ============");
            System.out.printf("Start time:%s\n", DateUtils.getDate());
            try {
                getMyIp();
                System.out.printf("GetIP:%s\n", this.myIp);
                System.out.printf("Has IP changed:%s\n", !this.deployed);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                deployDns();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.printf("End time:%s\n", DateUtils.getDate());
            System.out.println("==== DDNS-for-Cloudflare >>>>>>>>>>>>\n");
        }, 1, 5, TimeUnit.SECONDS);
    }

    private void deployDns() throws IOException {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("match", "all");
        paramMap.put("type", "A");
        paramMap.put("name", this.domain);
        List<DnsRecords> dnsRecords = cloudflare.dnsRecords.getListDnsRecord(getZone().getId(), paramMap);
        CreateDnsRecord createDnsRecord = new CreateDnsRecord();
        createDnsRecord.setType("A");
        createDnsRecord.setName(this.domain);
        createDnsRecord.setContent(this.myIp);
        createDnsRecord.setTtl(120);
        createDnsRecord.setProxied(false);
        createDnsRecord.setPriority(10);
        if (BeanUtils.isEmpty(dnsRecords)) {
            System.out.printf("DeployDns-CreateDnsRecord:%s\n", this.myIp);
            cloudflare.dnsRecords.createDnsRecord(getZone().getId(), createDnsRecord);
        } else {
            System.out.printf("DeployDns-UpdateDnsRecord:%s\n", this.myIp);
            cloudflare.dnsRecords.updateDnsRecord(getZone().getId(), dnsRecords.get(0).getId(), createDnsRecord);
        }
        this.deployed = true;
    }

    private Zone getZone() throws IOException {
        if (zone != null) {
            return zone;
        }
        List<Zone> zones = cloudflare.zone.getListZones();
        for (Zone zone : zones
        ) {
            if (zoneName.equals(zone.getName())) {
                this.zone = zone;
            }
        }
        if (zone == null) {
            throw new RuntimeException("No zone found in Cloudflare: " + zoneName);
        }
        return zone;
    }

    private void getMyIp() throws IOException {
        HttpRequest request = HttpRequest.create()
                .url("https://ip.renfei.net").useSSL();
        HttpResult result = HttpUtils.get(request);
        JSONObject jsonObject = JSON.parseObject(result.getResponseText());
        String ip = jsonObject.getString("clientIP");
        if (!ip.equals(this.myIp)) {
            this.myIp = ip;
            this.deployed = false;
        }
    }
}
