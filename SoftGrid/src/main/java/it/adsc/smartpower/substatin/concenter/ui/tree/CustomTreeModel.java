package it.adsc.smartpower.substatin.concenter.ui.tree;

import javax.swing.tree.TreeNode;

/**
 * Created by prageethmahendra on 12/9/2016.
 */
public class CustomTreeModel extends javax.swing.tree.DefaultTreeModel {

    public CustomTreeModel(TreeNode root) {
        super(root);
    }

    public CustomTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }


}
