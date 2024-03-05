package dieg0407.tools.jcli.services.engines.mvn;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import dieg0407.tools.jcli.TestTypes;
import java.io.File;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@Tag(TestTypes.INTEGRATION)
@TestInstance(Lifecycle.PER_CLASS)
public class PomBuildFileIntegrationTest {

  PomBuildFile pomBuildFile;

  @BeforeAll
  void setup() {
    PomFetcher mockedPomFetcher = new PomFetcher() {
      @Override
      public File fetch() {
        return new File("");
      }
    };
    pomBuildFile = new PomBuildFile(mockedPomFetcher, new XmlMapper());
  }
}
