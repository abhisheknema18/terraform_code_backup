package com.amtsybex.fieldreach.services.endpoint.rest.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amtsybex.fieldreach.backend.model.DefectNote;
import com.amtsybex.fieldreach.backend.model.DefectSet;
import com.amtsybex.fieldreach.backend.model.DefectSetDetail;
import com.amtsybex.fieldreach.backend.model.FormatInputDef;
import com.amtsybex.fieldreach.backend.model.GenNumValid;
import com.amtsybex.fieldreach.backend.model.HPCUsers;
import com.amtsybex.fieldreach.backend.model.Item;
import com.amtsybex.fieldreach.backend.model.MeasureCategories;
import com.amtsybex.fieldreach.backend.model.PublishedScripts;
import com.amtsybex.fieldreach.backend.model.Script;
import com.amtsybex.fieldreach.backend.model.ScriptCategory;
import com.amtsybex.fieldreach.backend.model.ScriptRefItem;
import com.amtsybex.fieldreach.backend.model.ScriptVersions;
import com.amtsybex.fieldreach.backend.model.UnitOfMeasure;
import com.amtsybex.fieldreach.backend.model.UomNumValid;
import com.amtsybex.fieldreach.backend.service.ScriptService;
import com.amtsybex.fieldreach.backend.service.UserService;
import com.amtsybex.fieldreach.backend.service.ValidationTypeService;
import com.amtsybex.fieldreach.services.endpoint.common.ScriptController;
import com.amtsybex.fieldreach.services.endpoint.rest.impl.utils.HPCUsersUtil;
import com.amtsybex.fieldreach.services.messages.response.GetScriptResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptListResponse;
import com.amtsybex.fieldreach.services.messages.response.ScriptQuestionDefinitionResponse;
import com.amtsybex.fieldreach.services.messages.types.scriptdef.ItemData;
import com.amtsybex.fieldreach.services.resource.exception.ResourceNotFoundException;
import com.amtsybex.fieldreach.utils.impl.Common;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RestScriptControllerImplTest {

    @Mock
    private ScriptController scriptController;

    @Mock
    private ScriptService scriptService;

    @Mock
    private ValidationTypeService validationTypeService;

    @Mock
    private UserService userService;
    
    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter jackson2XmlHttpMessageConverter;

    @Autowired
    @InjectMocks
    @Spy
    private RestScriptControllerImpl restScriptController;

    private final XmlMapper xmlMapper = new XmlMapper();

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(restScriptController)
                .setMessageConverters(jackson2HttpMessageConverter, jackson2XmlHttpMessageConverter)
                .build();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    @Test
    void getScriptSource_shouldReturnContents_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        Integer scriptId = 1;
        when(scriptController.getScriptSourceContents(scriptId, null)).thenReturn("test");
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/" + scriptId)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetScriptResponse response = xmlMapper.readValue(contentAsString, GetScriptResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("test", response.getScriptFileSource());
        assertNull(response.getError());
    }

    @Test
    void getScriptSource_shouldReturnContents_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        Integer scriptId = 1;
        when(scriptController.getScriptSourceContents(scriptId, null)).thenReturn("test");

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/" + scriptId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetScriptResponse response = jsonMapper.readValue(contentAsString, GetScriptResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("test", response.getScriptFileSource());
        assertNotNull(response.getError());
    }
    
    @Test
    void getScriptSource_shouldThrowFileNotFound_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        Integer scriptId = 1;
        when(scriptController.getScriptSourceContents(scriptId, null)).thenThrow(ResourceNotFoundException.class);
        
        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/" + scriptId)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetScriptResponse response = xmlMapper.readValue(contentAsString, GetScriptResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(null, response.getScriptFileSource());
        assertEquals("FileNotFoundException",response.getError().getErrorCode());
    }

    
    @Test
    void getScriptSource_shouldThrowFileNotFound_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        Integer scriptId = 1;
        when(scriptController.getScriptSourceContents(scriptId, null)).thenThrow(ResourceNotFoundException.class);

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/" + scriptId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        GetScriptResponse response = jsonMapper.readValue(contentAsString, GetScriptResponse.class);
        assertFalse(response.isSuccess());
        assertEquals(null, response.getScriptFileSource());
        assertEquals("FileNotFoundException",response.getError().getErrorCode());
    }

    @Test
    void getScriptQuestionDefinition_shouldGenerateScriptQuestionDefinitionResponseBasedOnScriptData_whenAcceptHeaderInXml() throws Exception {
        // Arrange
        int scriptId = 1;
        int seqNo = 1;
        PodamFactory factory = new PodamFactoryImpl();
        Item item = factory.manufacturePojoWithFullData(Item.class);
        item.setItemType(Common.QUESTION_TYPE_DATE);
        DefectSet defectSet = factory.manufacturePojo(DefectSet.class);
        DefectSetDetail defectSetDetail = factory.manufacturePojo(DefectSetDetail.class);
        DefectNote defectNote = factory.manufacturePojo(DefectNote.class);
        FormatInputDef formatInputDef = factory.manufacturePojo(FormatInputDef.class);
        GenNumValid genNumValid = factory.manufacturePojo(GenNumValid.class);
        UomNumValid uomNumValid = factory.manufacturePojo(UomNumValid.class);
        MeasureCategories measureCategories = factory.manufacturePojo(MeasureCategories.class);
        UnitOfMeasure unitOfMeasure = factory.manufacturePojo(UnitOfMeasure.class);
        ScriptRefItem scriptRefItem = factory.manufacturePojo(ScriptRefItem.class);
        
        when(scriptService.findScriptItem(any(), anyInt(), anyInt())).thenReturn(item);
        when(scriptService.findByDefectSetName(any(), any())).thenReturn(defectSet);
        when(scriptService.findDefectSetDetailByDefectSetName(any(), any())).thenReturn(List.of(defectSetDetail));
        when(scriptService.findDefectNoteByDefectCode(any(), any())).thenReturn(defectNote);
        when(scriptService.findFormatInputDef(any(), any(), anyInt())).thenReturn(List.of(formatInputDef));
        when(scriptService.findGenNumValidbyScriptIdSequenceNo(any(), any(), anyInt())).thenReturn(genNumValid);
        when(scriptService.findUomNumValidbyScriptIdSequenceNo(any(), any(), anyInt())).thenReturn(List.of(uomNumValid));
        when(scriptService.findByUOMCatID(any(), anyInt())).thenReturn(measureCategories);
        when(scriptService.findUOMByUOMCatID(any(), anyInt())).thenReturn(List.of(unitOfMeasure));
        when(scriptService.findScriptRefItemsbyScriptIdSequenceNumber(any(), anyInt(), anyInt())).thenReturn(List.of(scriptRefItem));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/" + scriptId + "/question/" + seqNo)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ScriptQuestionDefinitionResponse response = xmlMapper.readValue(contentAsString, ScriptQuestionDefinitionResponse.class);
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        ItemData responseItemData = response.getItemData();
        assertEquals(defectSetDetail.getId().getDefectCode(), responseItemData.getDEFECTS().get(0).getDEFECTCODE());
        assertEquals(defectSetDetail.getId().getDefectSetName(), responseItemData.getDEFECTS().get(0).getDEFECTDESCRIPTION());
        
        assertEquals(defectNote.getId().getDefectCode(), responseItemData.getDEFECTS().get(0).getDEFECTNOTE().get(0).getDEFECTCODE());
        assertEquals(defectNote.getId().getSnid(), responseItemData.getDEFECTS().get(0).getDEFECTNOTE().get(0).getSNID());
        
        assertEquals(defectSet.getDefectSetDesc(), responseItemData.getDEFECTSET().getDEFECTSETDESC());
        assertEquals(defectSet.getId(), responseItemData.getDEFECTSET().getDEFECTSETNAME());
        
        assertEquals(defectSetDetail.getId().getDefectCode(), responseItemData.getDEFECTSET().getDEFECTSETDETAIL().get(0).getDEFECTCODE());
        assertEquals(defectSetDetail.getId().getDefectSetName(), responseItemData.getDEFECTSET().getDEFECTSETDETAIL().get(0).getDEFECTSETNAME());
        
        assertEquals(item.getFormatInputDefMe().getMinEntry(), responseItemData.getFORMATINPUTDEFME().get(0).getMINENTRY());
        assertEquals(seqNo, responseItemData.getFORMATINPUTDEFME().get(0).getSEQUENCENUMBER());
        assertEquals(scriptId, responseItemData.getFORMATINPUTDEFME().get(0).getSCRIPTID());
        
        assertEquals(scriptId, responseItemData.getITEM().getSCRIPTID());
        assertEquals(seqNo, responseItemData.getITEM().getSEQUENCENUMBER());
        assertEquals(item.getAlternateRef(), responseItemData.getITEM().getALTERNATEREF());
        assertEquals(item.getItemText(), responseItemData.getITEM().getITEMTEXT());
        assertEquals(item.getItemType(), responseItemData.getITEM().getITEMTYPE());
        assertEquals(item.getInputType(), responseItemData.getITEM().getINPUTTYPE());
        assertEquals(item.getiLevel(), responseItemData.getITEM().getILEVEL());
        assertEquals(item.getFieldSize(), responseItemData.getITEM().getFIELDSIZE());
        assertEquals(item.getPrecision(), responseItemData.getITEM().getPRECISION());
        assertEquals(item.getValidation(), responseItemData.getITEM().getVALIDATION());
        assertEquals(item.getDataRef(), responseItemData.getITEM().getDATAREF());
        assertEquals(item.getDefectSetName(), responseItemData.getITEM().getDEFECTSETNAME());
        assertEquals(item.getUomCatId(), responseItemData.getITEM().getUOMCATID());
        assertEquals(item.getRelWeight(), responseItemData.getITEM().getRELWEIGHT());

        assertEquals(uomNumValid.getId().getSequenceNumber(), responseItemData.getITEM().getUOMNUMVALID().get(0).getSEQUENCENUMBER());
        assertEquals(uomNumValid.getId().getUomType(), responseItemData.getITEM().getUOMNUMVALID().get(0).getUOMTYPE());
        assertEquals(uomNumValid.getLowerLimit(), responseItemData.getITEM().getUOMNUMVALID().get(0).getLOWERLIMIT());
        assertEquals(uomNumValid.getUpperLimit(), responseItemData.getITEM().getUOMNUMVALID().get(0).getUPPERLIMIT());
        assertEquals(uomNumValid.getLowerWarning(), responseItemData.getITEM().getUOMNUMVALID().get(0).getLOWERWARNING());
        assertEquals(uomNumValid.getUpperWarning(), responseItemData.getITEM().getUOMNUMVALID().get(0).getUPPERWARNING());

        assertEquals(formatInputDef.getId().getSequenceNo(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getSEQUENCENUMBER());
        assertEquals(formatInputDef.getId().getCharPos(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getCHARPOS());
        assertEquals(formatInputDef.getAllowedChars(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getALLOWEDCHARS());
        assertEquals(formatInputDef.getCharType(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getCHARTYPE());
        assertEquals(formatInputDef.getCaseField(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getCASE());

        assertEquals(genNumValid.getId().getSequenceNumber(), responseItemData.getITEM().getGENNUMVALID().getSEQUENCENUMBER());
        assertEquals(genNumValid.getLowerLimit(), responseItemData.getITEM().getGENNUMVALID().getLOWERLIMIT());
        assertEquals(genNumValid.getUpperLimit(), responseItemData.getITEM().getGENNUMVALID().getUPPERLIMIT());
        assertEquals(genNumValid.getLowerWarning(), responseItemData.getITEM().getGENNUMVALID().getLOWERWARNING());
        assertEquals(genNumValid.getUpperWarning(), responseItemData.getITEM().getGENNUMVALID().getUPPERWARNING());
        
        assertEquals(measureCategories.getCatName(), responseItemData.getMEASURECATEGORIES().get(0).getCATNAME());
        assertEquals(measureCategories.getId(), responseItemData.getMEASURECATEGORIES().get(0).getUOMCATID());
        
        assertEquals(unitOfMeasure.getId().getUomCatId(), responseItemData.getMEASURECATEGORIES().get(0).getUNITOFMEASURE().get(0).getUOMCATID());
        assertEquals(unitOfMeasure.getId().getUomType(), responseItemData.getMEASURECATEGORIES().get(0).getUNITOFMEASURE().get(0).getUOMTYPE());
        assertEquals(unitOfMeasure.getPref(), responseItemData.getMEASURECATEGORIES().get(0).getUNITOFMEASURE().get(0).getPREF());
        
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getId(), responseItemData.getREFCAT().get(0).getREFCATID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getRefCatDesc(), responseItemData.getREFCAT().get(0).getREFCATDESC());
        
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFSUBCATID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefSubCatDesc(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFSUBCATDESC());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFCATID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefSubCatType(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFSUBCATTYPE());
        
        assertEquals(scriptRefItem.getRefItem().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getREFID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getREFSUBCATID());
        assertEquals(scriptRefItem.getRefItem().getRefDesc(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getREFDESC());
        assertEquals(scriptRefItem.getRefItem().getOtherRef(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getOTHERREF());
    }

    @Test
    void getScriptQuestionDefinition_shouldGenerateScriptQuestionDefinitionResponseBasedOnScriptData_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        int scriptId = 1;
        int seqNo = 1;
        PodamFactory factory = new PodamFactoryImpl();
        Item item = factory.manufacturePojoWithFullData(Item.class);
        item.setItemType(Common.QUESTION_TYPE_DATE);
        DefectSet defectSet = factory.manufacturePojo(DefectSet.class);
        DefectSetDetail defectSetDetail = factory.manufacturePojo(DefectSetDetail.class);
        DefectNote defectNote = factory.manufacturePojo(DefectNote.class);
        FormatInputDef formatInputDef = factory.manufacturePojo(FormatInputDef.class);
        GenNumValid genNumValid = factory.manufacturePojo(GenNumValid.class);
        UomNumValid uomNumValid = factory.manufacturePojo(UomNumValid.class);
        MeasureCategories measureCategories = factory.manufacturePojo(MeasureCategories.class);
        UnitOfMeasure unitOfMeasure = factory.manufacturePojo(UnitOfMeasure.class);
        ScriptRefItem scriptRefItem = factory.manufacturePojo(ScriptRefItem.class);

        when(scriptService.findScriptItem(any(), anyInt(), anyInt())).thenReturn(item);
        when(scriptService.findByDefectSetName(any(), any())).thenReturn(defectSet);
        when(scriptService.findDefectSetDetailByDefectSetName(any(), any())).thenReturn(List.of(defectSetDetail));
        when(scriptService.findDefectNoteByDefectCode(any(), any())).thenReturn(defectNote);
        when(scriptService.findFormatInputDef(any(), any(), anyInt())).thenReturn(List.of(formatInputDef));
        when(scriptService.findGenNumValidbyScriptIdSequenceNo(any(), any(), anyInt())).thenReturn(genNumValid);
        when(scriptService.findUomNumValidbyScriptIdSequenceNo(any(), any(), anyInt())).thenReturn(List.of(uomNumValid));
        when(scriptService.findByUOMCatID(any(), anyInt())).thenReturn(measureCategories);
        when(scriptService.findUOMByUOMCatID(any(), anyInt())).thenReturn(List.of(unitOfMeasure));
        when(scriptService.findScriptRefItemsbyScriptIdSequenceNumber(any(), anyInt(), anyInt())).thenReturn(List.of(scriptRefItem));

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/" + scriptId + "/question/" + seqNo)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ScriptQuestionDefinitionResponse response = jsonMapper.readValue(contentAsString, ScriptQuestionDefinitionResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        ItemData responseItemData = response.getItemData();
        assertEquals(defectSetDetail.getId().getDefectCode(), responseItemData.getDEFECTS().get(0).getDEFECTCODE());
        assertEquals(defectSetDetail.getId().getDefectSetName(), responseItemData.getDEFECTS().get(0).getDEFECTDESCRIPTION());

        assertEquals(defectNote.getId().getDefectCode(), responseItemData.getDEFECTS().get(0).getDEFECTNOTE().get(0).getDEFECTCODE());
        assertEquals(defectNote.getId().getSnid(), responseItemData.getDEFECTS().get(0).getDEFECTNOTE().get(0).getSNID());

        assertEquals(defectSet.getDefectSetDesc(), responseItemData.getDEFECTSET().getDEFECTSETDESC());
        assertEquals(defectSet.getId(), responseItemData.getDEFECTSET().getDEFECTSETNAME());

        assertEquals(defectSetDetail.getId().getDefectCode(), responseItemData.getDEFECTSET().getDEFECTSETDETAIL().get(0).getDEFECTCODE());
        assertEquals(defectSetDetail.getId().getDefectSetName(), responseItemData.getDEFECTSET().getDEFECTSETDETAIL().get(0).getDEFECTSETNAME());

        assertEquals(item.getFormatInputDefMe().getMinEntry(), responseItemData.getFORMATINPUTDEFME().get(0).getMINENTRY());
        assertEquals(seqNo, responseItemData.getFORMATINPUTDEFME().get(0).getSEQUENCENUMBER());
        assertEquals(scriptId, responseItemData.getFORMATINPUTDEFME().get(0).getSCRIPTID());

        assertEquals(scriptId, responseItemData.getITEM().getSCRIPTID());
        assertEquals(seqNo, responseItemData.getITEM().getSEQUENCENUMBER());
        assertEquals(item.getAlternateRef(), responseItemData.getITEM().getALTERNATEREF());
        assertEquals(item.getItemText(), responseItemData.getITEM().getITEMTEXT());
        assertEquals(item.getItemType(), responseItemData.getITEM().getITEMTYPE());
        assertEquals(item.getInputType(), responseItemData.getITEM().getINPUTTYPE());
        assertEquals(item.getiLevel(), responseItemData.getITEM().getILEVEL());
        assertEquals(item.getFieldSize(), responseItemData.getITEM().getFIELDSIZE());
        assertEquals(item.getPrecision(), responseItemData.getITEM().getPRECISION());
        assertEquals(item.getValidation(), responseItemData.getITEM().getVALIDATION());
        assertEquals(item.getDataRef(), responseItemData.getITEM().getDATAREF());
        assertEquals(item.getDefectSetName(), responseItemData.getITEM().getDEFECTSETNAME());
        assertEquals(item.getUomCatId(), responseItemData.getITEM().getUOMCATID());
        assertEquals(item.getRelWeight(), responseItemData.getITEM().getRELWEIGHT());

        assertEquals(uomNumValid.getId().getSequenceNumber(), responseItemData.getITEM().getUOMNUMVALID().get(0).getSEQUENCENUMBER());
        assertEquals(uomNumValid.getId().getUomType(), responseItemData.getITEM().getUOMNUMVALID().get(0).getUOMTYPE());
        assertEquals(uomNumValid.getLowerLimit(), responseItemData.getITEM().getUOMNUMVALID().get(0).getLOWERLIMIT());
        assertEquals(uomNumValid.getUpperLimit(), responseItemData.getITEM().getUOMNUMVALID().get(0).getUPPERLIMIT());
        assertEquals(uomNumValid.getLowerWarning(), responseItemData.getITEM().getUOMNUMVALID().get(0).getLOWERWARNING());
        assertEquals(uomNumValid.getUpperWarning(), responseItemData.getITEM().getUOMNUMVALID().get(0).getUPPERWARNING());

        assertEquals(formatInputDef.getId().getSequenceNo(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getSEQUENCENUMBER());
        assertEquals(formatInputDef.getId().getCharPos(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getCHARPOS());
        assertEquals(formatInputDef.getAllowedChars(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getALLOWEDCHARS());
        assertEquals(formatInputDef.getCharType(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getCHARTYPE());
        assertEquals(formatInputDef.getCaseField(), responseItemData.getITEM().getFORMATINPUTDEF().get(0).getCASE());

        assertEquals(genNumValid.getId().getSequenceNumber(), responseItemData.getITEM().getGENNUMVALID().getSEQUENCENUMBER());
        assertEquals(genNumValid.getLowerLimit(), responseItemData.getITEM().getGENNUMVALID().getLOWERLIMIT());
        assertEquals(genNumValid.getUpperLimit(), responseItemData.getITEM().getGENNUMVALID().getUPPERLIMIT());
        assertEquals(genNumValid.getLowerWarning(), responseItemData.getITEM().getGENNUMVALID().getLOWERWARNING());
        assertEquals(genNumValid.getUpperWarning(), responseItemData.getITEM().getGENNUMVALID().getUPPERWARNING());

        assertEquals(measureCategories.getCatName(), responseItemData.getMEASURECATEGORIES().get(0).getCATNAME());
        assertEquals(measureCategories.getId(), responseItemData.getMEASURECATEGORIES().get(0).getUOMCATID());

        assertEquals(unitOfMeasure.getId().getUomCatId(), responseItemData.getMEASURECATEGORIES().get(0).getUNITOFMEASURE().get(0).getUOMCATID());
        assertEquals(unitOfMeasure.getId().getUomType(), responseItemData.getMEASURECATEGORIES().get(0).getUNITOFMEASURE().get(0).getUOMTYPE());
        assertEquals(unitOfMeasure.getPref(), responseItemData.getMEASURECATEGORIES().get(0).getUNITOFMEASURE().get(0).getPREF());

        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getId(), responseItemData.getREFCAT().get(0).getREFCATID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getRefCatDesc(), responseItemData.getREFCAT().get(0).getREFCATDESC());

        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFSUBCATID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefSubCatDesc(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFSUBCATDESC());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefCat().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFCATID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getRefSubCatType(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFSUBCATTYPE());

        assertEquals(scriptRefItem.getRefItem().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getREFID());
        assertEquals(scriptRefItem.getRefItem().getRefSubCat().getId(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getREFSUBCATID());
        assertEquals(scriptRefItem.getRefItem().getRefDesc(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getREFDESC());
        assertEquals(scriptRefItem.getRefItem().getOtherRef(), responseItemData.getREFCAT().get(0).getREFSUBCAT().get(0).getREFITEM().get(0).getOTHERREF());
    }

    @Test
    void getScriptList_shouldFetchScriptAndVersionDetailsAndReturnResponse_whenAcceptHeaderInXml() throws Exception {
        // Arrange
    	PublishedScripts stubbPublishedScripts = getStubbedPublishedScripts();
    	HPCUsers user = HPCUsersUtil.getUserDetails();
        Script stubbedScript = getStubbedScript();
        ScriptVersions stubVersion = getStubbedVersion();
        stubVersion.setScript(stubbedScript);
        stubVersion.setId(1);
        stubVersion.setVersionNumber(100);
        stubVersion.setDateCreated(100000);
        when(scriptService.findPublishedScript(any(), anyInt())).thenReturn(stubbPublishedScripts);
        when(scriptService.findOnlineScriptVersions(null)).thenReturn(List.of(stubVersion));
        doReturn(user).when(restScriptController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/list")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ScriptListResponse response = xmlMapper.readValue(contentAsString, ScriptListResponse.class);
        assertTrue(response.isSuccess());
        assertNull(response.getError());
        assertNotNull(response.getScriptList().get(0).getChecksum());
        assertEquals(stubVersion.getId().toString(), response.getScriptList().get(0).getScriptId());
        assertEquals(stubbedScript.getScriptCode(), response.getScriptList().get(0).getScriptCode());
        assertEquals(stubbedScript.getScriptDesc(), response.getScriptList().get(0).getScriptDesc());
        assertEquals(stubVersion.getVersionNumber(), response.getScriptList().get(0).getVersionNumber().intValue());
        assertEquals(stubVersion.getDateCreated(), response.getScriptList().get(0).getDateCreated().intValue());
    }

    @Test
    void getScriptList_shouldFetchScriptAndVersionDetailsAndReturnResponse_whenAcceptHeaderInJson() throws Exception {
        // Arrange
        Script stubbedScript = getStubbedScript();
        ScriptVersions stubVersion = getStubbedVersion();
        stubVersion.setScript(stubbedScript);
        HPCUsers user = HPCUsersUtil.getUserDetails();
        PublishedScripts stubbPublishedScripts = getStubbedPublishedScripts();
        stubVersion.setId(1);
        stubVersion.setVersionNumber(100);
        stubVersion.setDateCreated(100000);
        
        when(scriptService.findOnlineScriptVersions(null)).thenReturn(List.of(stubVersion));
        when(scriptService.findPublishedScript(any(), anyInt())).thenReturn(stubbPublishedScripts);
        doReturn(user).when(restScriptController).getUserDetailsFromUserPrincipal(any());

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/script/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ScriptListResponse response = jsonMapper.readValue(contentAsString, ScriptListResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getError());
        assertNotNull(response.getScriptList().get(0).getChecksum());
        assertEquals(stubVersion.getId().toString(), response.getScriptList().get(0).getScriptId());
        assertEquals(stubbedScript.getScriptCode(), response.getScriptList().get(0).getScriptCode());
        assertEquals(stubbedScript.getScriptDesc(), response.getScriptList().get(0).getScriptDesc());
        assertEquals(stubVersion.getVersionNumber(), response.getScriptList().get(0).getVersionNumber().intValue());
        assertEquals(stubVersion.getDateCreated(), response.getScriptList().get(0).getDateCreated().intValue());
    }

    private Script getStubbedScript() {
        Script script = new Script();
        script.setScriptCode("abc");
        script.setScriptDesc("desc");
        script.setScriptCatId(1);
        script.setCalcScore(100);
        script.setScoreThreshold(150);
        script.setStandalone(2);
        script.setId(123);
        script.setScriptCategory(new ScriptCategory("cat desc"));
        return script;
    }
    
    private ScriptVersions getStubbedVersion() {
    	ScriptVersions version = new ScriptVersions();
    	version.setId(1);
    	version.setOnlineStatus(1);
    	version.setVersionNumber(1);
   
    	return version;
    }
    
    private PublishedScripts getStubbedPublishedScripts() {
    	PublishedScripts publishedScript = new PublishedScripts();
    	publishedScript.setChecksum("Checksum");
    	
    	return publishedScript;
    }
}