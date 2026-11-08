import element.DirectoryNode;
import element.FileNode;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;
import visitor.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemVisitorTest {

    private DirectoryNode root;
    private DirectoryNode srcDir;
    private DirectoryNode testDir;
    private DirectoryNode emptyDir;

    @BeforeEach
    void setUp() {
        root = new DirectoryNode("project");
        srcDir = new DirectoryNode("src");
        testDir = new DirectoryNode("test");
        emptyDir = new DirectoryNode("empty");

        DirectoryNode mainDir = new DirectoryNode("main");
        mainDir.addChild(new FileNode("App.java", 1500));
        mainDir.addChild(new FileNode("AppConfig.java", 800));

        srcDir.addChild(mainDir);
        srcDir.addChild(new FileNode("module-info.java", 200));

        testDir.addChild(new FileNode("AppTest.java", 2000));

        root.addChild(srcDir);
        root.addChild(testDir);
        root.addChild(emptyDir);
        root.addChild(new FileNode("pom.xml", 500));
    }

    //T1 Both element types reach the correct overloaded visit methods
    @Test
    void testDoubleDispatchReachesCorrectOverloadedMethods() {
        class SpyVisitor implements Visitor {
            int fileVisits = 0;
            int directoryVisits = 0;

            @Override
            public void visit(FileNode file) {
                fileVisits++;
            }

            @Override
            public void visit(DirectoryNode directory) {
                directoryVisits++;
            }
        }

        SpyVisitor spy = new SpyVisitor();
        FileNode singleFile = new FileNode("single.txt", 100);
        DirectoryNode singleDir = new DirectoryNode("folder");

        singleFile.accept(spy);
        singleDir.accept(spy);

        assertEquals(1, spy.fileVisits);
        assertEquals(1, spy.directoryVisits);
    }

    //T2 Size accumulation across multiple nesting levels is correct
    @Test
    void testTotalSizeAccumulationAcrossNestingLevels() {
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);

        // 1500 + 800 + 200 + 2000 + 500 = 5000
        assertEquals(5000L, sizeVisitor.getTotalSize());
    }

    //T3 Search correctly combines both configurable filter conditions
    @Test
    void testSearchCombinesNameAndMinSizeThreshold() {
        SearchVisitor searchVisitor = new SearchVisitor("App", 1000);
        root.accept(searchVisitor);

        List<String> matches = searchVisitor.getMatchingPaths();
        assertEquals(2, matches.size());
        assertTrue(matches.contains("src/main/App.java"));
        assertTrue(matches.contains("test/AppTest.java"));
        assertFalse(matches.contains("src/main/AppConfig.java")); // 800 < 1000
    }

    //T4 Search produces correct, deterministic, root-relative paths
    @Test
    void testSearchProducesDeterministicLexicographicallySortedPaths() {
        SearchVisitor searchVisitor = new SearchVisitor(".java", 0);
        root.accept(searchVisitor);

        List<String> expectedOrder = List.of(
                "src/main/App.java",
                "src/main/AppConfig.java",
                "src/module-info.java",
                "test/AppTest.java"
        );
        assertEquals(expectedOrder, searchVisitor.getMatchingPaths());
    }

    //T5 Statistics are correct for nesting, counting and maximum depth
    @Test
    void testStatisticsVisitorCountsAndDepth() {
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);

        assertEquals(5, statsVisitor.getDirectoryCount());
        assertEquals(5, statsVisitor.getFileCount());
        assertEquals(3, statsVisitor.getMaxDepth());
    }

    //T6 Empty structures and no-match search cases are handled
    @Test
    void testEmptyStructuresAndNoMatchCases() {
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        emptyDir.accept(sizeVisitor);
        assertEquals(0L, sizeVisitor.getTotalSize());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        emptyDir.accept(statsVisitor);
        assertEquals(1, statsVisitor.getDirectoryCount());
        assertEquals(0, statsVisitor.getFileCount());
        assertEquals(0, statsVisitor.getMaxDepth());

        SearchVisitor noMatchSearch = new SearchVisitor("non_existent_file", 99999);
        root.accept(noMatchSearch);
        assertTrue(noMatchSearch.getMatchingPaths().isEmpty());
    }

    //T7 Repeat executions do not leak or multiply accumulated state
    @Test
    void testRepeatExecutionsDoNotLeakState() {
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        assertEquals(5000L, sizeVisitor.getTotalSize());

        root.accept(sizeVisitor);
        assertEquals(5000L, sizeVisitor.getTotalSize());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        root.accept(statsVisitor);
        assertEquals(5, statsVisitor.getFileCount());
        assertEquals(5, statsVisitor.getDirectoryCount());
    }

    //T8 Invalid tree mutations are rejected while valid nesting remains supported
    @Test
    void testInvalidTreeMutationsAreRejected() {
        // Negative file size
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -10));

        // Duplicate sibling name
        assertThrows(IllegalArgumentException.class, () -> root.addChild(new FileNode("pom.xml", 100)));

        // Multiple parent ownership
        FileNode sharedFile = new FileNode("shared.txt", 50);
        srcDir.addChild(sharedFile);
        assertThrows(IllegalStateException.class, () -> testDir.addChild(sharedFile));

        // Cycle prevention (adding root into its descendant)
        assertThrows(IllegalArgumentException.class, () -> srcDir.addChild(root));

        // Encapsulation check children collection is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> root.getChildren().clear());
    }
}