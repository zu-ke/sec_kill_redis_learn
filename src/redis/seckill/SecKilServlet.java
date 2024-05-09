package redis.seckill;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class SecKilServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求时，模拟生成一个uid
        String uid = new Random().nextInt(1000) + "";

        //获取用户要购买的票的编号
        String ticketNo = req.getParameter("ticketNo");

        //调用秒杀的方法
        boolean isOk = SecKillRedisByLua.doSecKill(uid, ticketNo);

        //返回
        resp.getWriter().print(isOk);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
