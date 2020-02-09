package ru.zeet.task1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class FileBrowser implements Runnable {
    private static Logger log = Logger.getLogger(FileJPanel.class.getName());
    ExecutorService executor;
    private ConcurrentLinkedDeque<FileQueue> queue;

    private DefaultMutableTreeNode root;
    DefaultTreeModel model;
    String filePath;
    String ext;
    String regexp;

    public class FileQueue {
        private File fileRoot;
        private DefaultMutableTreeNode node;

        public FileQueue(File fileRoot, DefaultMutableTreeNode node) {
            this.fileRoot = fileRoot;
            this.node = node;
        }

        public File getFileRoot() {
            return fileRoot;
        }

        public void setFileRoot(File fileRoot) {
            this.fileRoot = fileRoot;
        }

        public DefaultMutableTreeNode getNode() {
            return node;
        }

        public void setNode(DefaultMutableTreeNode node) {
            this.node = node;
        }
    }

    public FileBrowser(DefaultMutableTreeNode root, DefaultTreeModel model, String filePath, String ext, String regexp) {
        this.root = root;
        this.filePath = filePath;
        this.model = model;
        this.ext = ext;
        this.regexp = regexp;
    }

    @Override
    public void run() {

        File fileRoot = new File(filePath);
        queue = new ConcurrentLinkedDeque<>();

        // executor = Executors.newSingleThreadExecutor();
        //executor.submit(new CreateChildNodes(fileRoot, root));

        createChildrenRecu(fileRoot, root);
        log.info("search done");
    }


    /*public class CreateChildNodes implements Runnable {

        private DefaultMutableTreeNode root;

        private File fileRoot;

        public CreateChildNodes(File fileRoot, DefaultMutableTreeNode root) {
            this.fileRoot = fileRoot;
            this.root = root;

        }


        @Override
        public void run() {

            // createChildren(fileRoot, root);
            createChildrenRecu(fileRoot, root);
            log.info("search done");
        }

        private void createChildren(File fileRoot, DefaultMutableTreeNode node) {
            File[] files = fileRoot.listFiles();
            if (files == null) return;

            for (File file : files) {
                {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
                    //try { Thread.sleep(1000);} catch (InterruptedException e) {}

                    if (file.isDirectory()) {
                        node.add(childNode);
                        executor.submit(new CreateChildNodes(file, childNode));
                    } else if (file.getName().toLowerCase().endsWith(ext)) {
                        node.add(childNode);
                        model.nodeStructureChanged(node);
                        log.info(file.getAbsolutePath());
                    }
                }
            }
        }
    }*/


    private boolean createChildrenRecu(File fileRoot, DefaultMutableTreeNode node) {
        boolean logFinded = false;

        File[] files = fileRoot.listFiles();
        if (files == null) {
            return false;
        }

        for (File file : files) {
            {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
                //try { Thread.sleep(1000);} catch (InterruptedException e) {}

                if (file.isDirectory()) {
                    if (createChildrenRecu(file, childNode)) {
                        node.add(childNode);
                        //model.nodeStructureChanged(node);
                        //model.nodeStructureChanged(childNode);
                        logFinded = true;
                    }
                } else if (file.getName().toLowerCase().endsWith(ext)) {
                    FileMatcher fm = new FileMatcher(file, regexp);
                    if (fm.matches()) {
                        node.add(childNode);
                        model.nodeStructureChanged(node);
                        model.nodeStructureChanged(childNode);
                        log.info(file.getAbsolutePath());
                        logFinded = true;
                    }
                }
            }
        }
        return logFinded;
    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }


    public class FileNode {

        private File file;

        public FileNode(File file) {
            this.file = file;
        }

        @Override
        public String toString() {
            String name = file.getName();
            if (name.equals("")) {
                return file.getAbsolutePath();
            } else {
                return name;
            }
        }
    }

}