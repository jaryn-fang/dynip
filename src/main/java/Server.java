import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//
public class Server {
    public static void main(String[] args) {
        String ip = AliUtils.getAliIp();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new CheakIp(ip), 0,1, TimeUnit.MINUTES);
    }
}

class CheakIp implements Runnable {

    private String ip = "";
    private String reId = "3604589338286080";

    public CheakIp(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {
        System.out.println("执行检查....");
        String nowIp = Iputils.getV4IP();
        if(!ip.equals(nowIp)) {
            //调用接口
            System.out.println();
            Object obj = HttpUtils.doGetBackJson(AliUtils.update(nowIp, reId), null);
            if(obj != null) {
                ip = nowIp;
                System.out.println("更新了ip地址...." + ip);
            }
        }
    }
}
