package ru.zeet.task1;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AppSeacher extends JFrame {
    JTextField searchDirText;
    JTextField exportUrlTextField;
    JButton runButton;
    JTextField searchText;
    JTextField fileExtension;


    JTabbedPane tabbedPane;
    JFileChooser fileChooser;
    DefaultMutableTreeNode rootNode;
    DefaultTreeModel treeModel;
    JTree jTree;

    Thread fileSearchThread;

    Logger logger = Logger.getLogger(AppSeacher.class.getName());


    public AppSeacher() throws IOException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setTitle("File seacher");
        setLayout(new BorderLayout());

        getContentPane().add(createPanel1(), BorderLayout.NORTH);
        getContentPane().add(createPanelTree(), BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Info", new FileJPanel());

        content.add(createSearchPanel(), BorderLayout.NORTH);
        content.add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(content);
        tabbedPane.getSelectedComponent();

        fileChooser = new JFileChooser();

        setVisible(true);
    }

    JComponent createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton b = new JButton("Open");
        b.setPreferredSize(new Dimension(100, 20));
        b.addActionListener(new openFileActionListener());

        JButton selectAllButton = new JButton("Select all");
        selectAllButton.setPreferredSize(new Dimension(100, 20));
        selectAllButton.addActionListener(new selectAllButtonActionListener());
        JButton previousButton = new JButton("Previous ");
        previousButton.setPreferredSize(new Dimension(100, 20));
        previousButton.addActionListener(new previousButtonActionListener());
        JButton nextButton = new JButton("Next");
        nextButton.setPreferredSize(new Dimension(100, 20));
        nextButton.addActionListener(new nextButtonActionListener());

        searchPanel.add(b);
        searchPanel.add(selectAllButton);
        searchPanel.add(previousButton);
        searchPanel.add(nextButton);

        return searchPanel;
    }

    JComponent createPanelTree() {
        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());

        JLabel a = new JLabel("____________________________");
        //a.setSize(new Dimension(100, 100));
        //a.setVisible(false);

        treePanel.add(a, BorderLayout.NORTH);

        rootNode = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(rootNode);
        jTree = new JTree(treeModel);

        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //jTree.addTreeSelectionListener(new openFileActionListener());

        JScrollPane scrollPane = new JScrollPane(jTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        treePanel.add(scrollPane, BorderLayout.CENTER);

        return treePanel;
    }

    JComponent createPanel1() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(5, 1));

        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel("Start from:");
        urlLabel.setPreferredSize(new Dimension(100, 20));

        searchDirText = new JTextField();
        searchDirText.setColumns(20);
        searchDirText.setText("c:\\");

        p1.add(urlLabel);
        p1.add(searchDirText);

        // расширение файла
        JPanel p2 = new JPanel();
        p2.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel l2 = new JLabel("File extension:");
        l2.setPreferredSize(new Dimension(100, 20));
        fileExtension = new JTextField();
        fileExtension.setColumns(20);
        fileExtension.setText("log");

        p2.add(l2);
        p2.add(fileExtension);

        // текст для поиска
        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel j3 = new JLabel("Text to search:");
        j3.setPreferredSize(new Dimension(100, 20));

        searchText = new JTextField();
        searchText.setColumns(20);
        searchText.setText("info");

        p3.add(j3);
        p3.add(searchText);


        JPanel p4 = new JPanel();
        p4.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton selectPath = new JButton("Select path");
        selectPath.setPreferredSize(new Dimension(328, 20));
        selectPath.addActionListener(new selectPathActionListener());
        p4.add(selectPath);

        JPanel p5 = new JPanel();
        p5.setLayout(new FlowLayout(FlowLayout.LEFT));
        runButton = new JButton("Search");
        runButton.setPreferredSize(new Dimension(328, 20));
        runButton.addActionListener(new LoadActionListener());
        p5.add(runButton);

        topPanel.add(p1);
        topPanel.add(p2);
        topPanel.add(p3);
        topPanel.add(p4);
        topPanel.add(p5);


        return topPanel;
    }

    public class openFileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {

                if (jTree.getLastSelectedPathComponent() != null) {
                    Object[] path = jTree.getSelectionPath().getPath();
                    String fName = path[path.length - 1].toString();

                    String fPath = Arrays.stream(path).map(s -> s.toString()).collect(Collectors.joining("\\")).toString();

                    File f = new File(fPath);
                    if (f.isFile()) {
                        SwingUtilities.invokeLater(new Loader(fPath, fName, searchText.getText()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class selectPathActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    searchDirText.setText(fileChooser.getSelectedFile().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }


    public class LoadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                if (searchDirText.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(searchDirText, "Start from field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (fileExtension.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(fileExtension, "File extension field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (searchText.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(searchText, "Text to search field is empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                rootNode.removeAllChildren();
                treeModel.reload();
                rootNode.setUserObject(searchDirText.getText());

                if (fileSearchThread != null /*&& fileSearchThread.isAlive()*/) {
                    fileSearchThread.stop(); // interrupt();
                }

                FileBrowser fb = new FileBrowser(rootNode, treeModel, searchDirText.getText(), "." + fileExtension.getText(), searchText.getText());
                fileSearchThread = new Thread(fb);
                fileSearchThread.start();
                // SwingUtilities.invokeLater(fb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

   public class selectAllButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Object obj = tabbedPane.getSelectedComponent();
            if (obj instanceof FileJPanel) {
                FileJPanel pnl = (FileJPanel) obj;
                if (pnl.getSearchResultCount() == 0) {
                    JOptionPane.showMessageDialog(searchText, "Nothing found", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    pnl.selectAll();
                }
            }
        }
    }

    public class previousButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Object obj = tabbedPane.getSelectedComponent();
            if (obj instanceof FileJPanel) {
                FileJPanel pnl = (FileJPanel) obj;
                if (pnl.getSearchResultCount() == 0) {
                    JOptionPane.showMessageDialog(searchText, "Nothing found", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    pnl.previousSearch();
                }
            }
        }
    }

    public class nextButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Object obj = tabbedPane.getSelectedComponent();
            if (obj instanceof FileJPanel) {
                FileJPanel pnl = (FileJPanel) obj;
                if (pnl.getSearchResultCount() == 0) {
                    JOptionPane.showMessageDialog(searchText, "Nothing found", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    pnl.nextSearch();
                }
            }
        }
    }

    public class Loader extends SwingWorker<String, String> {
        private String filePath;
        private String fileName;
        private String regexp;
        FileJPanel panel;

        public String readFileAsString(String fileName) throws IOException {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        }

        public Loader(String filePath, String fileName, String regexp) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.regexp = regexp;
        }

        @Override
        protected String doInBackground() throws IOException {
            panel = new FileJPanel();
            panel.setText(readFileAsString(filePath), regexp);
            return null;
        }

        @Override
        protected void done() {

            tabbedPane.addTab(fileName, panel);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
            panel.goHome();
        }
    }
}
