package com.amtsybex.fieldreach.fdm.equipment;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.scope.WindowScoped;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.amtsybex.fieldreach.backend.exception.FRInstanceException;
import com.amtsybex.fieldreach.backend.exception.MaxResultsExceededException;
import com.amtsybex.fieldreach.backend.model.EquipmentData;
import com.amtsybex.fieldreach.backend.service.EquipmentService;
import com.amtsybex.fieldreach.fdm.PageCodebase;
import com.amtsybex.fieldreach.fdm.web.jsf.util.MessageHelper;

@Named
@WindowScoped
public class EquipmentHierarchy extends PageCodebase implements Serializable {

	private static final long serialVersionUID = -6500334344876373598L;

	private TreeNode equipmentHierarchyTreeView;
	private TreeNode equipmentHierarchyFirstChild;
	private TreeNode selectedNodes;
	private String currentParentEquipNo;
	private boolean parentFound;
	private List<EquipmentData> equipmentSearchList;

	@Inject
	private transient EquipmentService equipmentService;
	@Inject
	transient EquipmentInformation equipmentInformation;
	@Inject
	transient EquipmentDetails equipmentDetails;

	public void init(EquipmentData equipmentData) throws FRInstanceException {
		// adding the root null element
		this.equipmentHierarchyTreeView = new DefaultTreeNode(new EquipmentData(), null);

		if (equipmentData != null) {
			this.equipmentHierarchyFirstChild = new DefaultTreeNode(equipmentData, this.equipmentHierarchyTreeView);

			this.currentParentEquipNo = equipmentData.getEquipNo();
			this.equipmentHierarchyFirstChild.setExpanded(true);
			
			EquipmentData parentData = getEquipmentService().getEquipmentData(null, equipmentData.getParentEquipNo());
			this.parentFound = parentData != null ? true :false;

			getChildNodes(equipmentData.getEquipNo(), this.equipmentHierarchyFirstChild);

		}

	}

	public void getChildNodes(String equipNo, TreeNode treeNode) throws FRInstanceException {

		List<EquipmentData> data = getEquipmentService().getEquipmentDataForParentEquipment(null, equipNo);

		TreeNode childNode;

		if (data != null && data.size() > 0) {
			for (EquipmentData eq : data) {
				childNode = new DefaultTreeNode(eq, treeNode);
				if (eq.isHasChildren())
					new DefaultTreeNode(eq, childNode);				
			}
		}

	}

	public void onNodeExpand(NodeExpandEvent event) throws FRInstanceException {
		TreeNode currentNode = event.getTreeNode();
		EquipmentData currentData = (EquipmentData) currentNode.getData();
		currentNode.getChildren().clear();
		getChildNodes(currentData.getEquipNo(), currentNode);

	}
	
	public void onNodeCollapse(NodeCollapseEvent event) {
		event.getTreeNode().setExpanded(false);
		
	}

	public void getParentNode() throws FRInstanceException {

		EquipmentData rootNode = (EquipmentData) this.equipmentHierarchyFirstChild.getData();
		EquipmentData data = getEquipmentService().getEquipmentData(null, rootNode.getParentEquipNo());

		if (data != null) {
			TreeNode oldRoot = this.equipmentHierarchyFirstChild;
			// adding the root null element
			this.equipmentHierarchyTreeView = new DefaultTreeNode(new EquipmentData(), null);

			this.equipmentHierarchyFirstChild = new DefaultTreeNode(data, this.equipmentHierarchyTreeView);
			this.currentParentEquipNo = data.getEquipNo();
			this.equipmentHierarchyFirstChild.setExpanded(true);

			TreeNode temp = new DefaultTreeNode(oldRoot.getData(), this.equipmentHierarchyFirstChild);
			temp.setExpanded(true);
			recursiveAddChilds(oldRoot, temp);
			
			EquipmentData parentData = getEquipmentService().getEquipmentData(null, data.getParentEquipNo());
			this.parentFound = parentData != null ? true :false;

			List<EquipmentData> listData = getEquipmentService().getEquipmentDataForParentEquipment(null,
					((EquipmentData) data).getEquipNo());

			TreeNode childNode;

			if (listData != null && listData.size() > 0) {
				for (EquipmentData eq : listData) {
					if (!eq.getEquipNo().equalsIgnoreCase(((EquipmentData) oldRoot.getData()).getEquipNo())) {
						childNode = new DefaultTreeNode(eq, this.equipmentHierarchyFirstChild);
						if (eq.isHasChildren())
							new DefaultTreeNode(eq, childNode);
					}
				}
			}

		} else {
			MessageHelper.setGlobalWarnMessage("Asset has no parent item");

		}

	}

	public void recursiveAddChilds(TreeNode oldRoot, TreeNode parentNode) {
		TreeNode oldChild;
		for (int i = 0; i < oldRoot.getChildCount(); i++) {
			oldChild = oldRoot.getChildren().get(i);
			TreeNode newChild = new DefaultTreeNode(oldChild.getData(), parentNode);
			if (oldChild.isExpanded()) {
				newChild.setExpanded(true);
				recursiveAddChilds(oldChild, newChild);
			}else {
				//if node is not expanded and has child inside dummy data is inserted to show the navigation icon
				if(oldChild.getChildCount()>0)
					new DefaultTreeNode(oldChild.getData(), newChild);
			}
		}
	}
	
	public void findEquipmentByRowDetails(EquipmentData selectedEquipmentData) throws FRInstanceException, IOException, MaxResultsExceededException {
		equipmentDetails.setReturnHierarchy(true);
		equipmentDetails.initialise(selectedEquipmentData);
	}
	
	public void loadAssetInformation(String equipNo) throws FRInstanceException {
		equipmentInformation.populateEquipmentAssetInformation(equipNo);
	}

	public String getTitle() {
		return "Asset Hierarchy";
	}

	public TreeNode getEquipmentHierarchyTreeView() {
		return equipmentHierarchyTreeView;
	}

	public void setEquipmentHierarchyTreeView(TreeNode equipmentHierarchyTreeView) {
		this.equipmentHierarchyTreeView = equipmentHierarchyTreeView;
	}

	public TreeNode getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public EquipmentService getEquipmentService() {
		return equipmentService;
	}

	public void setEquipmentService(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	public TreeNode getEquipmentHierarchyFirstChild() {
		return equipmentHierarchyFirstChild;
	}

	public void setEquipmentHierarchyFirstChild(TreeNode equipmentHierarchyFirstChild) {
		this.equipmentHierarchyFirstChild = equipmentHierarchyFirstChild;
	}

	public String getCurrentParentEquipNo() {
		return currentParentEquipNo;
	}

	public void setCurrentParentEquipNo(String currentParentEquipNo) {
		this.currentParentEquipNo = currentParentEquipNo;
	}

	public EquipmentInformation getEquipmentInformation() {
		return equipmentInformation;
	}

	public void setEquipmentInformation(EquipmentInformation equipmentInformation) {
		this.equipmentInformation = equipmentInformation;
	}

	public boolean isParentFound() {
		return parentFound;
	}

	public void setParentFound(boolean parentFound) {
		this.parentFound = parentFound;
	}

	public List<EquipmentData> getEquipmentSearchList() {
		return equipmentSearchList;
	}

	public void setEquipmentSearchList(List<EquipmentData> equipmentSearchList) {
		this.equipmentSearchList = equipmentSearchList;
	}


}
