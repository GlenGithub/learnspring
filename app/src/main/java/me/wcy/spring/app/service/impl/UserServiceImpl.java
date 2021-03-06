package me.wcy.spring.app.service.impl;

import com.alibaba.fastjson.JSON;
import me.wcy.spring.app.entity.User;
import me.wcy.spring.app.vo.UserVO;
import me.wcy.spring.app.common.ResponseCode;
import me.wcy.spring.app.common.ServiceRuntimeException;
import me.wcy.spring.app.dao.UserDAO;
import me.wcy.spring.app.service.TokenService;
import me.wcy.spring.app.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.Timestamp;

/**
 * Created by hzwangchenyan on 2017/9/7.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TokenService tokenService;

    @Override
    public long register(String username, String password, String phoneNumber, String nickname, String signature) throws ServiceRuntimeException {
        User origin = userDAO.queryByUsername(username);
        if (origin != null) {
            throw new ServiceRuntimeException(ResponseCode.GLOBAL_ILLEGAL_REQUEST, "user exist");
        }

        User user = new User();
        user.setUsername(username);
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        user.setPassword(md5Password);
        user.setPhone_number(phoneNumber);
        user.setNickname(nickname);
        user.setSignature(signature);
        user.setDb_create_time(new Timestamp(System.currentTimeMillis()));
        user.setDb_update_time(new Timestamp(System.currentTimeMillis()));
        try {
            return userDAO.insert(user);
        } catch (DataAccessException e) {
            LOGGER.error("insert user error. " + JSON.toJSONString(user));
            throw new ServiceRuntimeException(ResponseCode.GLOBAL_SERVER_ERROR, "register error", e);
        }
    }

    @Override
    public UserVO login(String username, String password) throws ServiceRuntimeException {
        try {
            User user = userDAO.queryByUsername(username);
            if (user == null) {
                throw new ServiceRuntimeException(ResponseCode.GLOBAL_DATA_NOT_EXIST, "user not exist");
            }
            if (!StringUtils.equalsIgnoreCase(user.getPassword(), password)) {
                throw new ServiceRuntimeException(ResponseCode.USER_AUTH_FAILED, "username or password error");
            }

            String token = tokenService.createToken(user.getId());
            UserVO userVO = UserVO.newUserVO(user);
            userVO.setToken(token);
            return userVO;
        } catch (DataAccessException e) {
            LOGGER.error("query user error. u=" + username + ", p=" + password, e);
            throw new ServiceRuntimeException(ResponseCode.GLOBAL_SERVER_ERROR, "login error", e);
        }
    }
}
