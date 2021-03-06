package seedu.superta.commons.util;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.superta.model.SuperTaClient;
import seedu.superta.storage.XmlAdaptedStudent;
import seedu.superta.storage.XmlAdaptedTag;
import seedu.superta.storage.XmlSerializableSuperTaClient;
import seedu.superta.testutil.StudentBuilder;
import seedu.superta.testutil.SuperTaClientBuilder;
import seedu.superta.testutil.TestUtil;

public class XmlUtilTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "XmlUtilTest");
    private static final Path EMPTY_FILE = TEST_DATA_FOLDER.resolve("empty.xml");
    private static final Path MISSING_FILE = TEST_DATA_FOLDER.resolve("missing.xml");
    private static final Path VALID_FILE = TEST_DATA_FOLDER.resolve("validAddressBook.xml");
    private static final Path MISSING_PERSON_FIELD_FILE = TEST_DATA_FOLDER.resolve("missingPersonField.xml");
    private static final Path INVALID_PERSON_FIELD_FILE = TEST_DATA_FOLDER.resolve("invalidPersonField.xml");
    private static final Path VALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("validPerson.xml");
    private static final Path TEMP_FILE = TestUtil.getFilePathInSandboxFolder("tempAddressBook.xml");

    private static final String INVALID_PHONE = "9482asf424";

    private static final String VALID_NAME = "Hans Muster";
    private static final String VALID_PHONE = "9482424";
    private static final String VALID_EMAIL = "hans@example";
    private static final String VALID_STUDENT_ID = "A0166733Y";
    private static final List<XmlAdaptedTag> VALID_TAGS = Collections.singletonList(new XmlAdaptedTag("friends"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getDataFromFile_nullFile_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.getDataFromFile(null, SuperTaClient.class);
    }

    @Test
    public void getDataFromFile_nullClass_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.getDataFromFile(VALID_FILE, null);
    }

    @Test
    public void getDataFromFile_missingFile_fileNotFoundException() throws Exception {
        thrown.expect(FileNotFoundException.class);
        XmlUtil.getDataFromFile(MISSING_FILE, SuperTaClient.class);
    }

    @Test
    public void getDataFromFile_emptyFile_dataFormatMismatchException() throws Exception {
        thrown.expect(JAXBException.class);
        XmlUtil.getDataFromFile(EMPTY_FILE, SuperTaClient.class);
    }

    @Test
    public void getDataFromFile_validFile_validResult() throws Exception {
        SuperTaClient dataFromFile = XmlUtil
                .getDataFromFile(VALID_FILE, XmlSerializableSuperTaClient.class)
                .toModelType();
        assertEquals(9, dataFromFile.getStudentList().size());
    }

    @Test
    public void xmlAdaptedPersonFromFile_fileWithMissingPersonField_validResult() throws Exception {
        XmlAdaptedStudent actualPerson = XmlUtil.getDataFromFile(
                MISSING_PERSON_FIELD_FILE, XmlAdaptedStudentWithRootElement.class);
        XmlAdaptedStudent expectedPerson = new XmlAdaptedStudent(
                null, VALID_PHONE, VALID_EMAIL, VALID_STUDENT_ID, VALID_TAGS);
        assertEquals(expectedPerson, actualPerson);
    }

    @Test
    public void xmlAdaptedPersonFromFile_fileWithInvalidPersonField_validResult() throws Exception {
        XmlAdaptedStudent actualPerson = XmlUtil.getDataFromFile(
                INVALID_PERSON_FIELD_FILE, XmlAdaptedStudentWithRootElement.class);
        XmlAdaptedStudent expectedPerson = new XmlAdaptedStudent(
                VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_STUDENT_ID, VALID_TAGS);
        assertEquals(expectedPerson, actualPerson);
    }

    @Test
    public void xmlAdaptedPersonFromFile_fileWithValidPerson_validResult() throws Exception {
        XmlAdaptedStudent actualPerson = XmlUtil.getDataFromFile(
                VALID_PERSON_FILE, XmlAdaptedStudentWithRootElement.class);
        XmlAdaptedStudent expectedPerson = new XmlAdaptedStudent(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_STUDENT_ID, VALID_TAGS);
        assertEquals(expectedPerson, actualPerson);
    }

    @Test
    public void saveDataToFile_nullFile_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.saveDataToFile(null, new SuperTaClient());
    }

    @Test
    public void saveDataToFile_nullClass_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.saveDataToFile(VALID_FILE, null);
    }

    @Test
    public void saveDataToFile_missingFile_fileNotFoundException() throws Exception {
        thrown.expect(FileNotFoundException.class);
        XmlUtil.saveDataToFile(MISSING_FILE, new SuperTaClient());
    }

    @Test
    public void saveDataToFile_validFile_dataSaved() throws Exception {
        FileUtil.createFile(TEMP_FILE);
        XmlSerializableSuperTaClient dataToWrite = new XmlSerializableSuperTaClient(new SuperTaClient());
        XmlUtil.saveDataToFile(TEMP_FILE, dataToWrite);
        XmlSerializableSuperTaClient dataFromFile = XmlUtil
                .getDataFromFile(TEMP_FILE, XmlSerializableSuperTaClient.class);
        assertEquals(dataToWrite, dataFromFile);

        SuperTaClientBuilder builder = new SuperTaClientBuilder(new SuperTaClient());
        dataToWrite = new XmlSerializableSuperTaClient(
                builder.withPerson(new StudentBuilder().build()).build());

        XmlUtil.saveDataToFile(TEMP_FILE, dataToWrite);
        dataFromFile = XmlUtil.getDataFromFile(TEMP_FILE, XmlSerializableSuperTaClient.class);
        assertEquals(dataToWrite, dataFromFile);
    }

    /**
     * Test class annotated with {@code XmlRootElement} to allow unmarshalling of .xml data to {@code XmlAdaptedStudent}
     * objects.
     */
    @XmlRootElement(name = "student")
    private static class XmlAdaptedStudentWithRootElement extends XmlAdaptedStudent {}
}
