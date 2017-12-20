package com.websocket.test;

import com.websocket.SocketMessage;
import net.sf.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created by tangliang on 2017/12/8.
 */
public class ServerEncoder implements Encoder.Text<SocketMessage> {
    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public String encode(SocketMessage messagepojo) throws EncodeException {
        try {
            JSONObject json = new JSONObject();
            json.put("obj", messagepojo);
            return json.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }
}
