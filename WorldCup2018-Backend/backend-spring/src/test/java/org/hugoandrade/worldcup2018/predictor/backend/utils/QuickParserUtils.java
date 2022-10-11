package org.hugoandrade.worldcup2018.predictor.backend.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

public final class QuickParserUtils {

    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = QuickParserUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private QuickParserUtils() {
        throw new RuntimeException("this is a utility class");
    }

    public static String format(Object loginData) {
        if (loginData == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            //Convert object to JSON string
            return mapper.writeValueAsString(loginData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parse(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        return parse(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static <T> T parse(Class<T> clazz, MvcResult mvcResult) throws UnsupportedEncodingException {
        return parse(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static <T> T parse(Class<T> clazz, String data) {
        return parse(data, clazz);
    }

    public static <T> T parse(String data, Class<T> clazz) {
        if (data == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            //Convert object to JSON string
            return mapper.readValue(data, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parse(MvcResult mvcResult, TypeReference<T> clazz) throws UnsupportedEncodingException {
        return parse(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static <T> T parse(TypeReference<T> clazz, MvcResult mvcResult) throws UnsupportedEncodingException {
        return parse(mvcResult.getResponse().getContentAsString(), clazz);
    }

    public static <T> T parse(String data, TypeReference<T> clazz) {
        if (data == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            //Convert object to JSON string
            return mapper.readValue(data, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> parseList(Class<T> clazz, MvcResult mvcResult) throws UnsupportedEncodingException {
        return parseList(clazz, mvcResult.getResponse().getContentAsString());
    }

    public static <T> List<T> parseList(MvcResult mvcResult, Class<T> clazz) throws UnsupportedEncodingException {
        return parseList(clazz, mvcResult.getResponse().getContentAsString());
    }

    public static <T> List<T> parseList(String data, Class<T> clazz) {
        return parseList(clazz, data);
    }

    public static <T> List<T> parseList(Class<T> clazz, String data) {
        if (data == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);

        try {
            //Convert object to JSON string
            return mapper.readValue(data, collectionType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
