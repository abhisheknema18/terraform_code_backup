package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection.APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON;
import static com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection.APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.gf.packages.PackageItem;
import com.amtsybex.fieldreach.gf.packages.PackageRelease;
import com.amtsybex.fieldreach.packages.PackageIndex;
import com.amtsybex.fieldreach.packages.gfspec.GFSpecPackageVersion;
import com.amtsybex.fieldreach.services.messages.response.PackageInfoCollection;
import com.amtsybex.fieldreach.services.messages.response.PackageUpdate;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageType;
import com.amtsybex.fieldreach.services.messages.types.packages.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestPackagesControllerImplTest {

	private MockMvc mockMvc;
	
	private final ObjectMapper jsonMapper = new ObjectMapper();
	
	@Autowired
    private PackageIndex packageIndex;
	
    @Autowired
    @InjectMocks
    private RestPackagesControllerImpl restPackageController;
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restPackageController).build();
    }
    
    
    @Test
    void packages_shouldGetEmptyList_whenNoPackageAvailable() throws Exception {
    	
    	// Arrange
    	//when(packageIndex.getPackages(any())).thenReturn(null);
    	packageIndex.setPackages(new ArrayList<PackageItem>());
    	
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        PackageInfoCollection response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageInfoCollection.class);
        
        assertEquals(0, response.getPackages().size());
        
    }
    
    @Test
    void packages_shouldGetRecentPackages_whenAllNotSet() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full ),
    			createPackageItem("map", 1, 0, 10, PackageType.Full )
    		).collect(Collectors.toList()));
    	
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        PackageInfoCollection response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageInfoCollection.class);
        
        assertEquals(2, response.getPackages().size());
        assertEquals(1, response.getPackages().stream().filter(p -> p.getId().equals("data")).count());
        assertEquals(1, response.getPackages().stream().filter(p -> p.getId().equals("map")).count());
    }
    
    @Test
    void packages_shouldGetAllPackages_whenAllSetToTrue() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full ),
    			createPackageItem("map", 1, 0, 10, PackageType.Full )
    		).collect(Collectors.toList()));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages?all=true").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        PackageInfoCollection response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageInfoCollection.class);
        
        assertEquals(3, response.getPackages().size());
        assertEquals(2, response.getPackages().stream().filter(p -> p.getId().equals("data")).count());
        assertEquals(1, response.getPackages().stream().filter(p -> p.getId().equals("map")).count());
    }
    
    @Test
    void packagesById_shouldGetEmptyList_whenNoPackageAvailable() throws Exception {
    	
    	// Arrange
    	//when(packageIndex.getPackages(any())).thenReturn(null);
    	packageIndex.setPackages(new ArrayList<PackageItem>());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages/data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        PackageInfoCollection response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageInfoCollection.class);
        
        assertEquals(0, response.getPackages().size());
        
    }
    
    @Test
    void packagesById_shouldGetAllPackagesForID_whenAllSetToTrue() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full )
    		).collect(Collectors.toList()));
    	
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages/data?all=true").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        PackageInfoCollection response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageInfoCollection.class);
        
        assertEquals(2, response.getPackages().size());
        assertEquals(2, response.getPackages().stream().filter(p -> p.getId().equals("data")).count());
        assertEquals(0, response.getPackages().stream().filter(p -> p.getId().equals("map")).count());
        
        PackageVersion version = response.getPackages().stream().filter(p -> p.getId().equals("data")).findFirst().get().getVersion();
        assertEquals(Integer.valueOf(1), version.getMajorVersion());
        assertEquals(Integer.valueOf(1), version.getMinorVersion());

    }
    
    @Test
    void packagesById_shouldGetRecentPackagesForId_whenAllNotSet() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full )
    		).collect(Collectors.toList()));
    	
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages/data").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        PackageInfoCollection response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageInfoCollection.class);
        
        assertEquals(1, response.getPackages().size());
        assertEquals(1, response.getPackages().stream().filter(p -> p.getId().equals("data")).count());
        assertEquals(0, response.getPackages().stream().filter(p -> p.getId().equals("map")).count());
        
        PackageVersion version = response.getPackages().stream().filter(p -> p.getId().equals("data")).findFirst().get().getVersion();
        assertEquals(Integer.valueOf(1), version.getMajorVersion());
        assertEquals(Integer.valueOf(1), version.getMinorVersion());
        
    }
    
    
    @Test
    void packagesUpdate_shouldGetBadRequest_whenNoFromInvalid() throws Exception {
    	
    	// Arrange

        // Act Assert
        this.mockMvc.perform(get("/packages/data/2/1/update?from=1.1.1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
    
    @Test
    void packagesUpdate_shouldGetLatest_whenAllFullPackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full ),
    			createPackageItem("data", 1, 2, 10, PackageType.Full ),
    			createPackageItem("data", 2, 0, 10, PackageType.Full ),
    			createPackageItem("data", 2, 1, 10, PackageType.Full ),
    			createPackageItem("data", 2, 2, 10, PackageType.Full )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/2/2/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(1, response.getSteps().size());
    	assertEquals(new PackageVersion(2,2), response.getSteps().get(0).getVersion());
        
    }
    
    @Test
    void packagesUpdate_shouldGet21_whenAllFullPackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full ),
    			createPackageItem("data", 1, 2, 10, PackageType.Full ),
    			createPackageItem("data", 2, 0, 10, PackageType.Full ),
    			createPackageItem("data", 2, 1, 10, PackageType.Full ),
    			createPackageItem("data", 2, 2, 10, PackageType.Full )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/2/1/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(1, response.getSteps().size());
    	assertEquals(new PackageVersion(2,1), response.getSteps().get(0).getVersion());
        
    }
    
    @Test
    void packagesUpdate_shouldGetThreeSteps_whenOneDeltaPackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full ),
    			createPackageItem("data", 1, 2, 10, PackageType.Full ),
    			createPackageItem("data", 2, 0, 10, PackageType.Full ),
    			createPackageItem("data", 2, 1, 10, PackageType.Full ),
    			createPackageItem("data", 2, 2, 10, PackageType.Delta ),
    			createPackageItem("data", 2, 3, 10, PackageType.Delta )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/2/3/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(3, response.getSteps().size());
    	assertEquals(new PackageVersion(2,1), response.getSteps().get(0).getVersion());
    	assertEquals(new PackageVersion(2,2), response.getSteps().get(1).getVersion());
    	assertEquals(new PackageVersion(2,3), response.getSteps().get(2).getVersion());
        
    }
    
    
    @Test
    void packagesUpdate_shouldGetTwoSteps_whenOneDeltaPackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Full ),
    			createPackageItem("data", 1, 2, 10, PackageType.Full ),
    			createPackageItem("data", 2, 0, 10, PackageType.Full ),
    			createPackageItem("data", 2, 1, 10, PackageType.Full ),
    			createPackageItem("data", 2, 2, 10, PackageType.Delta ),
    			createPackageItem("data", 2, 3, 10, PackageType.Delta )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/2/2/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(2, response.getSteps().size());
    	assertEquals(new PackageVersion(2,1), response.getSteps().get(0).getVersion());
    	assertEquals(new PackageVersion(2,2), response.getSteps().get(1).getVersion());
        
    }
    
    @Test
    void packagesUpdate_shouldGetOneStep_whenToVersionOnePackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 2, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 3, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 4, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 5, 10, PackageType.Full ),
    			createPackageItem("data", 2, 0, 10, PackageType.Full ),
    			createPackageItem("data", 2, 1, 10, PackageType.Full ),
    			createPackageItem("data", 2, 2, 10, PackageType.Delta )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/1/5/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(1, response.getSteps().size());
    	assertEquals(new PackageVersion(1,5), response.getSteps().get(0).getVersion());
        
    }
    
    @Test
    void packagesUpdate_shouldGetFullPlusDeltaSteps_whenNoDeltaOnFullTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 2, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 3, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 4, 10, PackageType.Full ),
    			createPackageItem("data", 1, 5, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 6, 10, PackageType.Delta )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/1/6/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(3, response.getSteps().size());
    	assertEquals(new PackageVersion(1,4), response.getSteps().get(0).getVersion());
    	assertEquals(PackageType.Full, response.getSteps().get(0).getType());
    	assertEquals(new PackageVersion(1,5), response.getSteps().get(1).getVersion());
    	assertEquals(new PackageVersion(1,6), response.getSteps().get(2).getVersion());
        
    }
    
    @Test
    void packagesUpdate_shouldGetDeltaSteps_whenSizeIsBestPackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 2, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 3, 10, PackageType.Delta ),
    			createFullPackageItem("data", 1, 4, 100, true, 10 ),
    			createPackageItem("data", 1, 5, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 6, 10, PackageType.Delta )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/1/6/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(5, response.getSteps().size());
    	assertEquals(new PackageVersion(1,2), response.getSteps().get(0).getVersion());
    	assertEquals(new PackageVersion(1,3), response.getSteps().get(1).getVersion());
    	assertEquals(new PackageVersion(1,4), response.getSteps().get(2).getVersion());
    	assertEquals(PackageType.Delta, response.getSteps().get(2).getType());
    	assertEquals(new PackageVersion(1,5), response.getSteps().get(3).getVersion());
    	assertEquals(new PackageVersion(1,6), response.getSteps().get(4).getVersion());
        
    }
    
    @Test
    void packagesUpdate_shouldGetFullPlusDeltaSteps_whenSizeIsBestPackageTypes() throws Exception {
    	
    	// Arrange
    	packageIndex.setPackages(Stream.of(
    			createPackageItem("data", 1, 0, 10, PackageType.Full ),
    			createPackageItem("data", 1, 1, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 2, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 3, 10, PackageType.Delta ),
    			createFullPackageItem("data", 1, 4, 10, true, 100 ),
    			createPackageItem("data", 1, 5, 10, PackageType.Delta ),
    			createPackageItem("data", 1, 6, 10, PackageType.Delta )
    		).collect(Collectors.toList()));
    
        // Act 
    	MvcResult mvcResult = this.mockMvc.perform(get("/packages/data/1/6/update?from=1.1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
    	PackageUpdate response = jsonMapper.readValue(mvcResult.getResponse().getContentAsString(), PackageUpdate.class);
    	
    	assertEquals(3, response.getSteps().size());
    	assertEquals(new PackageVersion(1,4), response.getSteps().get(0).getVersion());
    	assertEquals(PackageType.Full, response.getSteps().get(0).getType());
    	assertEquals(new PackageVersion(1,5), response.getSteps().get(1).getVersion());
    	assertEquals(new PackageVersion(1,6), response.getSteps().get(2).getVersion());
        
    }
    
    
    @Test
    void packagesById_shouldReturnValidContentType_json() throws Exception {
        
        // Arrange
        //when(packageIndex.getPackages(any())).thenReturn(null);
        packageIndex.setPackages(new ArrayList<PackageItem>());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages").accept(APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentTye = mvcResult.getResponse().getContentType();
        
        assertEquals(APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_JSON, contentTye);
        
    }
    
    @Test
    void packagesById_shouldReturnValidContentType_xml() throws Exception {
        
        // Arrange
        //when(packageIndex.getPackages(any())).thenReturn(null);
        packageIndex.setPackages(new ArrayList<PackageItem>());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/packages").accept(APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentTye = mvcResult.getResponse().getContentType();
        
        assertEquals(APPLICATION_VND_FIELDSMART_PACKAGEINFO_1_XML, contentTye);
        
    }
    
    private PackageItem createPackageItem(String id, int major, int minor, int size, PackageType type) {
    	
    	PodamFactory factory = new PodamFactoryImpl();
    	PackageItem pac = factory.manufacturePojo(PackageItem.class);
    	pac.setId(id);
    	pac.setVersion(new GFSpecPackageVersion(major,minor));
    	PackageRelease rel = factory.manufacturePojo(PackageRelease.class);
    	rel.setDownloadSize(size);
    	rel.setId(id);
    	rel.setType(type);
    	rel.setVersion(pac.getVersion());
    	pac.setReleases(Stream.of(rel).collect(Collectors.toList()));
    	
    	return pac;
    }
    
    private PackageItem createFullPackageItem(String id, int major, int minor, int size, boolean includeDelta, int deltaSize) {
    	
    	
    	PackageItem pac = this.createPackageItem(id, major, minor, size, PackageType.Full);
    	
    	if(includeDelta) {
    		PodamFactory factory = new PodamFactoryImpl();
    		PackageRelease rel = factory.manufacturePojo(PackageRelease.class);
    		rel.setDownloadSize(deltaSize);
    		rel.setId(id);
    		rel.setType(PackageType.Delta);
    		rel.setVersion(pac.getVersion());
    		
    		pac.getReleases().add(rel);
    	}
    	
    	
    	return pac;
    }

}
