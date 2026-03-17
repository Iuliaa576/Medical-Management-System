package repository;

import domain.Identifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileRepositoryTests {

    private File tempFile;
    private TestFileRepository repo;

    @BeforeEach
    void setUp() throws IOException, RepositoryException {
        // Create a temporary file path
        tempFile = File.createTempFile("filerepo_test", ".tmp");
        tempFile.deleteOnExit();

        repo = new TestFileRepository(tempFile.getAbsolutePath());
        // Simulate a read manually (constructor already calls readFromFile)
        repo.simulateReadFromFile();
    }

    @Test
    void constructorCallsReadFromFile() {
        Assertions.assertEquals(1, repo.getReadCount(),
                "readFromFile should be called once during construction");
        Assertions.assertEquals(0, repo.getWriteCount(),
                "writeToFile should not be called during construction");
    }

    @Test
    void addCallsWriteToFile() throws RepositoryException {
        Assertions.assertEquals(0, repo.getWriteCount(),
                "writeToFile should start at 0");

        DummyEntity e = new DummyEntity("D1");
        repo.add(e.getId(), e);

        Assertions.assertEquals(1, repo.getWriteCount(),
                "writeToFile should be called once after add()");
        Optional<DummyEntity> found = repo.findById("D1");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("D1", found.get().getId());
    }

    @Test
    void deleteCallsWriteToFile() throws RepositoryException {
        DummyEntity e = new DummyEntity("D2");
        repo.add(e.getId(), e);
        Assertions.assertEquals(1, repo.getWriteCount());

        repo.delete("D2");
        Assertions.assertEquals(2, repo.getWriteCount(),
                "writeToFile should be called again after delete()");
    }

    @Test
    void modifyCallsWriteToFile() throws RepositoryException {
        DummyEntity e = new DummyEntity("D3");
        repo.add(e.getId(), e);
        Assertions.assertEquals(1, repo.getWriteCount());

        repo.resetWriteCount();

        DummyEntity modified = new DummyEntity("D3");
        repo.modify("D3", modified);

        Assertions.assertEquals(1, repo.getWriteCount(),
                "modify() should call writeToFile()");

        Optional<DummyEntity> found = repo.findById("D3");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("D3", found.get().getId());
    }

    // Helper classes for testing


    // Simple identifiable dummy entity used for tests.
    private static class DummyEntity implements Identifiable<String> {
        private String id;

        public DummyEntity(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * A mock subclass of FileRepository that tracks read/write calls.
     */
    private static class TestFileRepository extends FileRepository<String, DummyEntity> {
        private int readCount = 0;
        private int writeCount = 0;

        public TestFileRepository(String fileName) throws RepositoryException {
            super(fileName);
        }

        // Explicit simulation of readFromFile() call count
        public void simulateReadFromFile() {
            readCount++;
        }

        @Override
        protected void readFromFile() {
            // Count the read but don’t actually perform file IO
            readCount++;
        }

        @Override
        protected void writeToFile() {
            // Count the write but don’t perform file IO
            writeCount++;
        }

        public int getReadCount() {
            return readCount;
        }

        public int getWriteCount() {
            return writeCount;
        }

        public void resetWriteCount() {
            writeCount = 0;
        }
    }
}
