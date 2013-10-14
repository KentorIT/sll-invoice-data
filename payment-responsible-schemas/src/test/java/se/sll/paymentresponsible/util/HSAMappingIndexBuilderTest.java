/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
