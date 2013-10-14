package se.sll.paymentresponsible.util;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class HSAMappingIndexBuilderTest {

    @Test
    public void parse_success() {
        HSAMappingIndexBuilder builder = new HSAMappingIndexBuilder()
        .withCommissionFile("kodserver_sample_data/SAMVERKS-REL.xml")
        .withCommissionTypeFile("kodserver_sample_data/UPPDRAGSTYP.xml")
        .withFacilityFile("kodserver_sample_data/AVD-REL.xml")
        .withMekFile("kodserver_sample_data/MEK.xml");
        
        final Map<String, List<HSAMapping>> index = builder.build();
      
        assertTrue(index.size() > 1000);
    }
}
