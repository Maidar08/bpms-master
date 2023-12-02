package mn.erin.bpm.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessTypeRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessType;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;

/**
 * @author Zorig
 */
@Ignore
// TODO :fix test
public class DefaultJdbcProcessTypeRepositoryTest
{
  private JdbcProcessTypeRepository mockJdbcProcessTypeRepository;
  private DefaultJdbcProcessTypeRepository defaultJdbcProcessTypeRepository;

  @Before
  public void initTest()
  {
    this.mockJdbcProcessTypeRepository = Mockito.mock(JdbcProcessTypeRepository.class);
    this.defaultJdbcProcessTypeRepository = new DefaultJdbcProcessTypeRepository(mockJdbcProcessTypeRepository);
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessTypeWithBlankProcessTypeId() throws BpmRepositoryException
  {
    defaultJdbcProcessTypeRepository.create("", "123", "name", ProcessDefinitionType.PROCESS);
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessTypeWithBlankProcessDefinitionId() throws BpmRepositoryException
  {
    defaultJdbcProcessTypeRepository.create("123", "", "name", ProcessDefinitionType.PROCESS);
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessTypeWithBlankName() throws BpmRepositoryException
  {
    defaultJdbcProcessTypeRepository.create("123", "123", "", ProcessDefinitionType.PROCESS);
  }

  @Test(expected = BpmRepositoryException.class)
  public void createProcessTypeWithNullProcessDefinitionType() throws BpmRepositoryException
  {
    defaultJdbcProcessTypeRepository.create("123", "123", "name", null);
  }

  @Test
  public void createProcessTypeSuccess() throws BpmRepositoryException
  {
    ProcessType result = defaultJdbcProcessTypeRepository.create("123", "123", "name", ProcessDefinitionType.PROCESS);

    Mockito.verify(mockJdbcProcessTypeRepository, Mockito.times(1)).insert("123", "123","PROCESS", "name");

    Assert.assertNotNull(result);
    Assert.assertEquals("123", result.getId().getId());
    Assert.assertEquals("123", result.getDefinitionKey());
    Assert.assertEquals("name", result.getName());
    Assert.assertEquals(ProcessDefinitionType.PROCESS, result.getProcessDefinitionType());
  }

  @Test(expected = NullPointerException.class)
  public void findByIdNullId()
  {
    defaultJdbcProcessTypeRepository.findById(null);
  }

  @Test
  public void findByIdEmptyOptional()
  {
    Mockito.when(mockJdbcProcessTypeRepository.findById("123")).thenReturn(Optional.empty());

    ProcessType result = defaultJdbcProcessTypeRepository.findById(new ProcessTypeId("123"));

    Mockito.verify(mockJdbcProcessTypeRepository, Mockito.times(1)).findById("123");

    Assert.assertNull(result);
  }

  @Test
  public void findByIdNonEmptyOptional()
  {
    JdbcProcessType jdbcProcessType = new JdbcProcessType();
    jdbcProcessType.setProcessTypeId("123");
    jdbcProcessType.setDefinitionKey("123");
    jdbcProcessType.setName("name");
    jdbcProcessType.setProcessDefinitionType("PROCESS");
    Mockito.when(mockJdbcProcessTypeRepository.findById("123")).thenReturn(Optional.of(jdbcProcessType));

    ProcessType result = defaultJdbcProcessTypeRepository.findById(new ProcessTypeId("123"));

    Mockito.verify(mockJdbcProcessTypeRepository, Mockito.times(1)).findById("123");

    Assert.assertNotNull(result);
    Assert.assertEquals("123", result.getId().getId());
    Assert.assertEquals("123", result.getDefinitionKey());
    Assert.assertEquals("name", result.getName());
    Assert.assertEquals(ProcessDefinitionType.PROCESS, result.getProcessDefinitionType());
    Assert.assertNull(result.getVersion());
  }

  @Test
  public void findAllQueryFindAllReturnsEmptyList()
  {
    Mockito.when(mockJdbcProcessTypeRepository.findAll()).thenReturn(new ArrayList<>());

    Collection<ProcessType> result = defaultJdbcProcessTypeRepository.findAll();

    Mockito.verify(mockJdbcProcessTypeRepository, Mockito.times(1)).findAll();

    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void findAllQueryFindAllReturnsNonEmptyList()
  {
    List<JdbcProcessType> jdbcProcessTypeList = new ArrayList<>();
    JdbcProcessType jdbcProcessType1 = new JdbcProcessType();
    jdbcProcessType1.setProcessTypeId("123");
    jdbcProcessType1.setProcessDefinitionType("PROCESS");
    jdbcProcessType1.setName("name");
    jdbcProcessType1.setDefinitionKey("123");
    jdbcProcessType1.setVersion("1");
    jdbcProcessTypeList.add(jdbcProcessType1);

    JdbcProcessType jdbcProcessType2 = new JdbcProcessType();
    jdbcProcessType2.setProcessTypeId("456");
    jdbcProcessType2.setProcessDefinitionType("CASE");
    jdbcProcessType2.setName("name");
    jdbcProcessType2.setDefinitionKey("123");
    jdbcProcessTypeList.add(jdbcProcessType2);

    Mockito.when(mockJdbcProcessTypeRepository.findAll()).thenReturn(jdbcProcessTypeList);

    Collection<ProcessType> result = defaultJdbcProcessTypeRepository.findAll();

    Mockito.verify(mockJdbcProcessTypeRepository, Mockito.times(1)).findAll();

    Assert.assertFalse(result.isEmpty());

    Iterator<ProcessType> resultProcessTypeIterator = result.iterator();
    ProcessType processType1 = resultProcessTypeIterator.next();
    ProcessType processType2 = resultProcessTypeIterator.next();

    Assert.assertEquals("123", processType1.getId().getId());
    Assert.assertEquals("123", processType1.getDefinitionKey());
    Assert.assertEquals("name", processType1.getName());
    Assert.assertEquals(ProcessDefinitionType.PROCESS, processType1.getProcessDefinitionType());
    Assert.assertEquals("1", processType1.getVersion());

    Assert.assertEquals("456", processType2.getId().getId());
    Assert.assertEquals("123", processType2.getDefinitionKey());
    Assert.assertEquals("name", processType2.getName());
    Assert.assertEquals(ProcessDefinitionType.CASE, processType2.getProcessDefinitionType());
    Assert.assertNull(processType2.getVersion());
  }
}
