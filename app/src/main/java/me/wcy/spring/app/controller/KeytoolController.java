package me.wcy.spring.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qcloud.cos.request.UploadFileRequest;
import me.wcy.spring.app.cos.COSManager;
import me.wcy.spring.app.common.Response;
import me.wcy.spring.app.common.ServiceRuntimeException;
import me.wcy.spring.app.service.KeytoolService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Created by hzwangchenyan on 2017/10/25.
 */
@RestController
@ConfigurationProperties(prefix = "keytool")
public class KeytoolController {
    @Autowired
    private KeytoolService keytoolService;

    private String javaPath;
    private String keyPath;

    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    @RequestMapping(value = "/api/keytool", method = RequestMethod.POST)
    public Response genKeystore(
            @RequestParam("alias") String alias,
            @RequestParam("storepass") String storepass,
            @RequestParam("keypass") String keypass,
            @RequestParam("validity") Integer validity,
            @RequestParam("name") String name,
            @RequestParam("organization") String organization,
            @RequestParam("city") String city,
            @RequestParam("province") String province,
            @RequestParam("countryCode") String countryCode
    ) {
        if (alias.contains(" ")
                || storepass.contains(" ")
                || keypass.contains(" ")
                || storepass.length() < 6
                || keypass.length() < 6
                || validity <= 0) {
            return new Response(400, "param format error");
        }

        String dir = keyPath;
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        } else if (dirFile.isFile()) {
            dirFile.delete();
            dirFile.mkdirs();
        }

        String fileName = "keystore_" + System.currentTimeMillis() + ".jks";
        String path = dir + fileName;
        File file = new File(path);

        try {
            keytoolService.genKeystore(javaPath, path, alias, storepass, keypass, validity, name, organization, city, province, countryCode);
        } catch (ServiceRuntimeException e) {
            return new Response(500, "generate keystore failed");
        }

        if (!file.exists()) {
            return new Response(500, "generate keystore failed");
        }

        UploadFileRequest uploadFileRequest = new UploadFileRequest(COSManager.BUCKET, "/" + file.getName(), file.getAbsolutePath());
        String uploadFileRet = COSManager.getCOSClient().uploadFile(uploadFileRequest);
        JSONObject resultObject = JSON.parseObject(uploadFileRet);
        Integer code = resultObject.getInteger("code");
        String message = resultObject.getString("message");
        if (code == null || code != 0) {
            return new Response(500, "upload file to COS failed, code: " + code + ", message: " + message);
        }

        JSONObject data = resultObject.getJSONObject("data");
        String downloadUrl = (data != null) ? data.getString("access_url") : null;
        if (StringUtils.isBlank(downloadUrl)) {
            return new Response(500, "access_url is empty");
        }

        return new Response(downloadUrl);
    }
}
