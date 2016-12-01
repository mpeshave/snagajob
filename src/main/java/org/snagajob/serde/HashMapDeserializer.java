package org.snagajob.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by peshave on 12/1/16.
 */
public class HashMapDeserializer extends JsonDeserializer<HashMap<String, String>> {

    public HashMap<String, String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        HashMap<String, String> ret = new HashMap<String, String>();

        ObjectCodec codec = jsonParser.getCodec();
        TreeNode node = codec.readTree(jsonParser);

        if (node.isArray()) {
            for (JsonNode n : (ArrayNode) node) {
                JsonNode id = n.get("Id");
                if (id != null) {
                    JsonNode name = n.get("Answer");
                    ret.put(id.asText(), name.asText());
                }
            }
            return ret;
        } else {
            throw deserializationContext.mappingException(String.format("%s is not a json array:", node));
        }
    }
}
