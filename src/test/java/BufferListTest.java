import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyuntao
 * @date 2025/02/14
 */
public class BufferListTest {

    @Test
    void test() {

    }
}


abstract class BaseBufferList {
    private final List<String> odpsBufferList = new ArrayList<>();

    public void add(String str) {
        odpsBufferList.add(str);
    }
}

class SonBufferList extends BaseBufferList {

    protected void saveToOdps(String extInfo) {
        add(extInfo);
    }
}

class Son2BufferList extends BaseBufferList {

    protected void saveToOdps(String extInfo) {
        add(extInfo);
    }
}