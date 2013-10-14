package se.sll.paymentresponsible.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static se.sll.paymentresponsible.util.CodeServiceXMLParser.CodeServiceEntryCallback;
import static se.sll.paymentresponsible.util.SimpleXMLElementParser.ElementMatcherCallback;

public class HSAMappingIndexBuilder {
    private static final Logger log = LoggerFactory.getLogger(HSAMappingIndexBuilder.class);

    private String mekFile;
    private String facilityFIle;
    private String commissionFile;
    private String commissionTypeFile;
    private Date maxAge = new Date(Long.MIN_VALUE);
    
    public HSAMappingIndexBuilder withMekFile(String mekFile) {
        this.mekFile = mekFile;
        return this;
    }

    public HSAMappingIndexBuilder withFacilityFile(String facilityFIle) {
        this.facilityFIle = facilityFIle;
        return this;
    }

    public HSAMappingIndexBuilder withCommissionFile(String commissionFile) {
        this.commissionFile = commissionFile;
        return this;
    }
    
    public HSAMappingIndexBuilder withCommissionTypeFile(String commissionTypeFile) {
        this.commissionTypeFile = commissionTypeFile;
        return this;
    }
    
    public HSAMappingIndexBuilder maxItemAge(Date maxAge) {
        this.maxAge = maxAge;
        return this;
    }
    
    Map<String, List<HSAMapping>> build() {
        log.info("build commissionTypeIndex from: {}", commissionTypeFile);
        final HashMap<String, CommissionType> commissionTypeIndex = createCommissionTypeIndex();
        log.info("commissionTypeIndex: {}", commissionTypeIndex.size());

        log.info("build commissionIndex from: {}", commissionFile);
        final HashMap<String, Commission> commissionIndex = createCommissionIndex(commissionTypeIndex);
        log.info("commissionIndex: {}", commissionIndex.size());
        
        log.info("build facilityIndex from: {}", facilityFIle);
        final HashMap<String, Facility> facilityIndex = createFacilityIndex(commissionIndex);
        log.info("facilityIndex: {}", facilityIndex.size());

        log.info("build hsaMapingIndex from: {}", mekFile);
        final Map<String, List<HSAMapping>> hsaIndex = createHSAIndex(facilityIndex);
        log.info("hsaMapingIndex: {}", hsaIndex.size());

        return hsaIndex;
    }
    
    protected Map<String, List<HSAMapping>> createHSAIndex(final HashMap<String, Facility> avdIndex) {
        SimpleXMLElementParser elementParser = new SimpleXMLElementParser(this.mekFile);
        final Map<String, List<HSAMapping>> map = new HashMap<String, List<HSAMapping>>();

        final Map<String, Integer> elements = new HashMap<String, Integer>();
        elements.put("Kombikakod", 1);
        elements.put("HSAId", 2);
        elements.put("FromDatum", 3);
        elements.put("TillDatum", 4);


        elementParser.parse("mappning", elements, new ElementMatcherCallback() {
            private HSAMapping mapping = null;
            @Override
            public void match(int element, String data) {
                switch (element) {
                case 1:
                    mapping.setFacility(avdIndex.get(data));
                    break;
                case 2:
                    mapping.setId(data);
                    break;
                case 3:
                    mapping.setValidFrom(HSAMapping.toDate(data));
                    break;
                case 4:
                    mapping.setValidTo(HSAMapping.toDate(data));
                    break;
                }
            }

            @Override
            public void end() {
                if (mapping.isValid() && mapping.getFacility() != null) {
                    List<HSAMapping> list = map.get(mapping.getId());
                    if (list == null) {
                        list = new ArrayList<HSAMapping>();
                        map.put(mapping.getId(), list);
                    }
                    list.add(mapping);
                }
            }

            @Override
            public void begin() {
                mapping = new HSAMapping();
            }
        });

        for (List<HSAMapping> l : map.values()) {
            Collections.sort(l);
        }

        return map;
    }


    protected HashMap<String, Facility> createFacilityIndex(final HashMap<String, Commission> samverksIndex) {
        final HashMap<String, Facility> index = new HashMap<String, Facility>();

        CodeServiceXMLParser parser = new CodeServiceXMLParser(this.facilityFIle, new CodeServiceEntryCallback() {
            @Override
            public void onCodeServiceEntry(CodeServiceEntry codeServiceEntry) {
                final List<String> codes = codeServiceEntry.getCodes("SAMVERKS");
                if (codes != null) {
                    // filter out non-existing SAMVERKS associations 
                    if (codes.size() == 1 && "0000".equals(codes.get(0))) {
                        return;
                    }
                    final Facility prev = index.get(codeServiceEntry.getId());
                    if (prev == null || prev.getValidTo().before(codeServiceEntry.getValidTo())) {
                        final Facility avd = new Facility();
                        avd.setId(codeServiceEntry.getId());
                        avd.setName(codeServiceEntry.getAttribute("shortname"));
                        avd.setValidFrom(codeServiceEntry.getValidFrom());
                        avd.setValidTo(codeServiceEntry.getValidTo());
                        final List<String> sl = codeServiceEntry.getCodes("SAMVERKS");
                        for (final String id : sl) {
                            final Commission samverks = samverksIndex.get(id);
                            if (samverks != null) {
                                avd.getCommissions().add(samverks);
                            }
                        }
                        index.put(codeServiceEntry.getId(), avd);
                    }
                }
            }
        });

        parser.extractAttribute("shortname");
        parser.extractCode("SAMVERKS");

        parser.parse();

        return index;
    }

    protected HashMap<String, Commission> createCommissionIndex(final HashMap<String, CommissionType> uppdragstypIndex) {
        final HashMap<String, Commission> index = new HashMap<String, Commission>();

        CodeServiceXMLParser parser = new CodeServiceXMLParser(this.commissionFile, new CodeServiceEntryCallback() {
            @Override
            public void onCodeServiceEntry(CodeServiceEntry codeServiceEntry) {
                final Commission prev = index.get(codeServiceEntry.getId());
                if (prev == null || prev.getValidTo().before(codeServiceEntry.getValidTo())) {
                    CommissionType uppdragstyp = null;
                    List<String> ul = codeServiceEntry.getCodes("UPPDRAGSTYP");
                    if (ul != null && ul.size() == 1) {
                        uppdragstyp = uppdragstypIndex.get(ul.get(0));
                    }
                    if (uppdragstyp != null) {
                        final Commission samverks = new Commission();
                        samverks.setId(codeServiceEntry.getId());
                        samverks.setName(codeServiceEntry.getAttribute("abbreviation"));
                        samverks.setCommissionType(uppdragstyp);
                        samverks.setValidFrom(codeServiceEntry.getValidFrom());
                        samverks.setValidTo(codeServiceEntry.getValidTo());
                        index.put(codeServiceEntry.getId(), samverks);
                    }
                }
            }
        });

        parser.extractAttribute("abbreviation");
        parser.extractCode("UPPDRAGSTYP");

        parser.parse();


        return index;
    }

    protected HashMap<String, CommissionType> createCommissionTypeIndex() {
        final HashMap<String, CommissionType> index = new HashMap<String, CommissionType>();

        CodeServiceXMLParser parser = new CodeServiceXMLParser(this.commissionTypeFile, new CodeServiceEntryCallback() {
            @Override
            public void onCodeServiceEntry(CodeServiceEntry codeServiceEntry) {
                final CommissionType prev = index.get(codeServiceEntry.getId());
                if (prev == null || prev.getValidTo().before(codeServiceEntry.getValidTo())) {
                    final CommissionType uppdragstyp = new CommissionType();
                    uppdragstyp.setId(codeServiceEntry.getId());
                    uppdragstyp.setName(codeServiceEntry.getAttribute("shortname"));
                    uppdragstyp.setValidFrom(codeServiceEntry.getValidFrom());
                    uppdragstyp.setValidTo(uppdragstyp.getValidTo());
                    index.put(codeServiceEntry.getId(), uppdragstyp);
                }
            }
        });

        parser.extractAttribute("shortname");

        parser.parse();

        return index;
    }    
}
