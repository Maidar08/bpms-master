package mn.erin.bpm.repository.jdbc.model;

/**
 * @author Tamir
 */

import java.sql.Clob;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ERIN_PRODUCT_LEASING")
public class JdbcProductLeasing
{
  @Id
  String id;
  String contractNum;
  String category;
  String subCategory;
  Clob description;
}
