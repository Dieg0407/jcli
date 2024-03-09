package dieg0407.tools.jcli.shared;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlMapperFactory {
  public static XmlMapper createInstance() {
    final var xmlMapper = new XmlMapper();
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    return xmlMapper;
  }
}
