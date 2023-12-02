package mn.erin.bpms.webapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

import mn.erin.bpm.domain.ohs.camunda.ProcessEngineProvider;

/**
 * @author Lkhagvadorj.A
 **/

public class BpmsDatabaseIndexImpl implements InitializingBean
{
    private final DataSource dataSource;
    private final ProcessEngineProvider processEngineProvider;
    // table name
    private final String PROCESS_PARAMETER = "PROCESS_PARAMETER";
    private final String PROCESS_REQUEST = "PROCESS_REQUEST";
    private final String PROCESS_REQUEST_PARAMETER = "PROCESS_REQUEST_PARAMETER";
    // column name
    private final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private final String PROCESS_REQUEST_ID = "PROCESS_REQUEST_ID";
    private final String PARAMETER_NAME = "PARAMETER_NAME";
    private final String ASSIGNED_USER_ID = "ASSIGNED_USER_ID";
    private final String PROCESS_REQUEST_STATE = "PROCESS_REQUEST_STATE";
    private final String PROCESS_TYPE_ID = "PROCESS_TYPE_ID";
    private final String GROUP_ID = "GROUP_ID";
    // index name
    private final String ERIN_PP_INST_ID = "ERIN_PP_INST_ID";
    private final String ERIN_PP_INST_ID_AND_PARAM_NAME = "ERIN_PP_INST_ID_AND_PARAM_NAME";
    private final String ERIN_PR_USER_ID_AND_STATE = "ERIN_PR_USER_ID_AND_STATE";
    private final String ERIN_PR_PROCESS_TYPE_AND_STATE = "ERIN_PR_PROCESS_TYPE_AND_STATE";
    private final String ERIN_PR_GROUP_ID_AND_STATE = "ERIN_PR_GROUP_ID_AND_STATE";
    private final String ERIN_PRP_REQUEST_ID = "ERIN_PRP_REQUEST_ID";

    private final List<String> PROCESS_PARAM_INDEX_LIST = Arrays.asList(ERIN_PP_INST_ID, ERIN_PP_INST_ID_AND_PARAM_NAME);
    private final List<String> PROCESS_REQUEST_INDEX_LIST = Arrays.asList(ERIN_PR_USER_ID_AND_STATE, ERIN_PR_PROCESS_TYPE_AND_STATE, ERIN_PR_GROUP_ID_AND_STATE);
    private final List<String> PROCESS_REQUEST_PARAMETER_INDEX_LIST = Collections.singletonList(ERIN_PRP_REQUEST_ID);

    BpmsDatabaseIndexImpl(DataSource dataSource, ProcessEngineProvider processEngineProvider)
    {
        this.dataSource = dataSource;
        this.processEngineProvider = processEngineProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        try(Connection con = dataSource.getConnection())
        {
            Statement st = con.createStatement();
            checkIndex(st, PROCESS_PARAMETER, PROCESS_PARAM_INDEX_LIST);
            checkIndex(st, PROCESS_REQUEST, PROCESS_REQUEST_INDEX_LIST);
            checkIndex(st, PROCESS_REQUEST_PARAMETER, PROCESS_REQUEST_PARAMETER_INDEX_LIST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkIndex(Statement statement, String tableName, List<String> requiredIndexList) throws SQLException
    {
        final ResultSet resultSet = statement.executeQuery(getIndexQuery(tableName));
        Map<String, String> indexMap = new HashMap<>();
        while (resultSet.next())
        {
            final String indexName = resultSet.getString("INDEX_NAME");
            final String columnName = resultSet.getString("COLUMN_NAME");
            indexMap.put(indexName, getColumn(indexMap, indexName, columnName));
        }

        for (String index : requiredIndexList)
        {
            if (indexMap.containsKey(index))
            {
                continue;
            }

            if (!checkIndexedColumn(indexMap, index))
            {
                statement.executeUpdate(getCreateIndexQuery(index, tableName, getColumnListByIndexName(index)));
            }
        }
    }

    private String getColumn(Map<String, String> indexMap, String indexName, String columnName)
    {
        if (indexMap.containsKey(indexName))
        {
            return indexMap.get(indexName) + "," + columnName;
        }
        return columnName;
    }

    private boolean checkIndexedColumn(Map<String, String> indexMap, String index)
    {
        final String columnList = getColumnListByIndexName(index);
        for (Map.Entry<String, String> entry: indexMap.entrySet())
        {
            final String value = entry.getValue();
            if (value.equals(columnList))
            {
                return true;
            }
        }
        return false;
    }

    private String getIndexQuery(String tableName)
    {
        return "SELECT INDEX_NAME, COLUMN_NAME FROM ALL_IND_COLUMNS WHERE TABLE_NAME = '" + tableName + "'";
        //    return "SELECT index_name FROM all_indexes WHERE table_name = '" + tableName + "'";
    }

    private String getCreateIndexQuery(String indexName, String tableName, String columnList)
    {
        return "CREATE INDEX " + indexName + " ON " + tableName + "(" + columnList + ")";
    }

    private String getColumnListByIndexName(String indexName)
    {
        switch (indexName)
        {
        case ERIN_PP_INST_ID:
            return PROCESS_INSTANCE_ID;
        case ERIN_PP_INST_ID_AND_PARAM_NAME:
            return PROCESS_INSTANCE_ID + "," + PARAMETER_NAME;
        case ERIN_PR_USER_ID_AND_STATE:
            return ASSIGNED_USER_ID + "," + PROCESS_REQUEST_STATE;
        case ERIN_PR_PROCESS_TYPE_AND_STATE:
            return PROCESS_TYPE_ID + "," + PROCESS_REQUEST_STATE;
        case ERIN_PR_GROUP_ID_AND_STATE:
            return GROUP_ID + "," + PROCESS_REQUEST_STATE;
        case ERIN_PRP_REQUEST_ID:
            return PROCESS_REQUEST_ID;
        default:
            return null;
        }
    }
}
