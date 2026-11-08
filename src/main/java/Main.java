import element.DirectoryNode;
import element.FileNode;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;

public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("workspace");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode emptyArchive = new DirectoryNode("archive");

        docs.addChild(new FileNode("report.pdf", 2500));
        docs.addChild(new FileNode("notes_report.txt", 400));

        src.addChild(new FileNode("Main.java", 1200));
        src.addChild(new FileNode("report_generator.java", 3100));

        root.addChild(docs);
        root.addChild(src);
        root.addChild(emptyArchive);
        root.addChild(new FileNode("README.md", 500));

        System.out.println("=== Advanced File System Demonstration ===");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("\n[1] Total Size Calculation:");
        System.out.println("Total size of files: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor("report", 1000);
        root.accept(searchVisitor);
        System.out.println("\n[2] Search Results (name contains 'report', size >= 1000B):");
        for (String path : searchVisitor.getMatchingPaths()) {
            System.out.println(" - " + path);
        }

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("\n[3] File System Statistics:");
        System.out.println("Total files: " + statsVisitor.getFileCount());
        System.out.println("Total directories: " + statsVisitor.getDirectoryCount());
        System.out.println("Max tree depth: " + statsVisitor.getMaxDepth());

        System.out.println("==========================================");
    }
}