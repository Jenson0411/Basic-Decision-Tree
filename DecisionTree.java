/**
 * This class enables the construction of a decision tree
 * 
 * @author Mehrdad Sabetzadeh, University of Ottawa
 * @author Guy-Vincent Jourdan, University of Ottawa
 *
 */

public class DecisionTree {

	private static class Node<E> {
		E data;
		Node<E>[] children;

		Node(E data) {
			this.data = data;
		}
	}

	Node<VirtualDataSet> root;

	/**
	 * @param data is the training set (instance of ActualDataSet) over which a
	 *             decision tree is to be built
	 */
	public DecisionTree(ActualDataSet data) {
		root = new Node<VirtualDataSet>(data.toVirtual());
		build(root);
	}

	/**
	 * The recursive tree building function
	 * 
	 * @param node is the tree node for which a (sub)tree is to be built
	 */
	@SuppressWarnings("unchecked")
	private void build(Node<VirtualDataSet> node) {
		// WRITE YOUR CODE HERE!

		boolean flag = true;
		GainInfoItem [] gain = InformationGainCalculator.calculateAndSortInformationGains(node.data);
		String aMax = gain[0].getAttributeName();
		AttributeType aMaxType = gain[0].getAttributeType();
		VirtualDataSet [] partitions;
		int index = -1;


		// Edge Cases
		if(node ==  null || node.data == null){
			throw new NullPointerException("The VirtualDataSet is null or the Node is Null");
		}

		else if (node.data.getNumberOfAttributes() == 0 || node.data.getNumberOfDatapoints() == 0){
			throw new IllegalArgumentException("The dataset has no attributes and/or datapoints");
		}

		//Base Cases 

		if(node.data.getNumberOfAttributes() == 1){
			flag = false;
		}

		else if(node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1).length == 1) {
			flag = false;
		}

		else if(gain[0].getGainValue() == 0.0){
			flag = false;
		}

		for(int i = 0; i<node.data.getNumberOfAttributes();i++){
			if(node.data.getUniqueAttributeValues(i).length >1){
				break;
			}

			else if(i == node.data.getNumberOfAttributes()-1){
				flag = false;
			}
		}

		// General Case 

		if(flag){
			if(aMaxType == AttributeType.NOMINAL){
				partitions =  node.data.partitionByNominallAttribute(node.data.getAttributeIndex(aMax));

			}
			else{
				String splitAt = gain[0].getSplitAt();
				String [] values = node.data.getAttribute(node.data.getAttributeIndex(aMax)).getValues();
				for(int i = 0; i< values.length; i++){
					if(values[i].equals(splitAt)){
						index = i;
						break;
					}
				}

				partitions = node.data.partitionByNumericAttribute(node.data.getAttributeIndex(aMax), index);		
				
			}

			node.children = new Node[partitions.length];


			for(int i = 0; i< partitions.length; i++){
				node.children[i] = new Node<VirtualDataSet>(partitions[i]); 
				build(node.children[i]);
			}	
		}
	}
	@Override
	public String toString() {
		return toString(root, 0);
	}

	/**
	 * The recursive toString function
	 * 
	 * @param node        is the tree node for which an if-else representation is to
	 *                    be derived
	 * @param indentDepth is the number of indenting spaces to be added to the
	 *                    representation
	 * @return an if-else representation of node
	 */
	private String toString(Node<VirtualDataSet> node, int indentDepth) {
		// WRITE YOUR CODE HERE!
		// Remove the following line once you have implemented the method
		StringBuffer string  = new StringBuffer();
		// Edge Cases
		if(node ==  null || node.data == null){
			throw new NullPointerException("The VirtualDataSet is null or the Node is Null");
		}

		else if (node.data.getNumberOfAttributes() == 0 || node.data.getNumberOfDatapoints() == 0){
			throw new IllegalArgumentException("The dataset has no attributes and/or datapoints");
		}

		// Base cases 

		if(node.data.getNumberOfAttributes() == 1){
			string.append(createIndent(indentDepth+1)+node.data.getAttribute(node.data.getNumberOfAttributes()-1).getName()+" = "+node.data.getValueAt(0, node.data.getNumberOfAttributes()-1)+"\n");
			return string.toString();
		}

		else if(node.data.getUniqueAttributeValues(node.data.getNumberOfAttributes()-1).length == 1) {
			string.append(createIndent(indentDepth+1)+node.data.getAttribute(node.data.getNumberOfAttributes()-1).getName()+" = "+node.data.getValueAt(0, node.data.getNumberOfAttributes()-1)+"\n");
			return string.toString();
		}

		for(int i = 0; i<node.data.getNumberOfAttributes();i++){
			if(node.data.getUniqueAttributeValues(i).length >1){
				break;
			}
			else if(i == node.data.getNumberOfAttributes()-1){
				string.append(createIndent(indentDepth+1)+node.data.getAttribute(node.data.getNumberOfAttributes()-1).getName()+" = "+node.data.getValueAt(0, node.data.getNumberOfAttributes()-1)+"\n");
				return string.toString();
			}
		}

		//General Case

		for(int i = 0; i< node.children.length; i++){
			if(i == 0){		
				string.append(createIndent(indentDepth)+"if ("+ node.children[i].data.getCondition()+") {\n" +toString(node.children[i], indentDepth+2)+createIndent(indentDepth)+"}\n");
			}
			else{
				string.append(createIndent(indentDepth)+"else if ("+ node.children[i].data.getCondition()+") {\n"+toString(node.children[i], indentDepth+2)+createIndent(indentDepth)+"}\n");
			}
		}

		return string.toString();
	}

	/**
	 * @param indentDepth is the depth of the indentation
	 * @return a string containing indentDepth spaces; the returned string (composed
	 *         of only spaces) will be used as a prefix by the recursive toString
	 *         method
	 */
	private static String createIndent(int indentDepth) {
		if (indentDepth <= 0) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indentDepth; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

	public static void main(String[] args) throws Exception {
	
		StudentInfo.display();

		if (args == null || args.length == 0) {
			System.out.println("Expected a file name as argument!");
			System.out.println("Usage: java DecisionTree <file name>");
			return;
		}


		String strFilename = args[0];

		ActualDataSet data = new ActualDataSet(new CSVReader(strFilename));

		DecisionTree dtree = new DecisionTree(data);

		System.out.println(dtree);
	}
}

