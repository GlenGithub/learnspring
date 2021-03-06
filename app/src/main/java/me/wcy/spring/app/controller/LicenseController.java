package me.wcy.spring.app.controller;

import me.wcy.spring.app.common.LicenseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by hzwangchenyan on 2017/9/20.
 */
@RestController
@RequestMapping("/rpc")
public class LicenseController {
    private static final Logger LOGGER = LogManager.getLogger(LicenseController.class);

    @RequestMapping("/obtainTicket.action")
    public String obtainTicket(String salt, String userName) throws Exception {
        LOGGER.info("obtainTicket.action");
        String content = "<ObtainTicketResponse>" +
                "<message></message>" +
                "<prolongationPeriod>607875500</prolongationPeriod>" +
                "<responseCode>OK</responseCode>" +
                "<salt>" + salt + "</salt>" +
                "<ticketId>1</ticketId>" +
                "<ticketProperties>licensee=" + userName + "	licenseType=0	</ticketProperties>" +
                "</ObtainTicketResponse>";
        return LicenseUtils.rsaSign(content);
    }

    @RequestMapping("/releaseTicket.action")
    public void releaseTicket(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @RequestMapping("ping.action")
    public String ping(String salt) throws Exception {
        String content = "<PingResponse>" +
                "<message></message>" +
                "<responseCode>OK</responseCode>" +
                "<salt>" + salt + "</salt>" +
                "</PingResponse>";
        return LicenseUtils.rsaSign(content);
    }
}
