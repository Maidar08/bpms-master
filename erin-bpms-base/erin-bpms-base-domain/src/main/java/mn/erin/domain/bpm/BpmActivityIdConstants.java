package mn.erin.domain.bpm;

/**
 * @author Tamir
 */
public final class BpmActivityIdConstants
{
  private BpmActivityIdConstants()
  {

  }

  public static final String TASK_ID_DOWNLOAD_CUSTOMER_INFO = "Task_1mppjgc";

  public static final String ACTIVITY_ID_SALARY_CALCULATION = "PlanItem_1ab00oa";
  public static final String ACTIVITY_ID_CALCULATION_STAGE = "PlanItem_0w2yo5k";
  public static final String ACTIVITY_ID_SCORING = "PlanItem_07ajbeu";
  public static final String ACTIVITY_ID_AMOUNT_CALCULATION = "PlanItem_0hv4otf";
  public static final String ACTIVITY_ID_MONGOL_BANK = "PlanItem_044khnk";
  public static final String ACTIVITY_ID_XYP = "PlanItem_1y5ja4m";

  public static final String ACTIVITY_ID_GENERATE_LOAN_DECISION = "PlanItem_0g81lne";
  public static final String ACTIVITY_ID_SEND_LOAN_DECISION = "PlanItem_033xob0";
  public static final String ACTIVITY_ID_MICRO_LOAN_DECISION = "process_task_micro_loan_decision";
  public static final String ACTIVITY_ID_MORTGAGE_LOAN_DECISION = "process_task_mortgage_loan_decision";

  // micro
  public static final String ACTIVITY_ID_MICRO_MONGOL_BANK = "process_task_micro_mongol_bank_enquire";
  public static final String ACTIVITY_ID_MICRO_XYP = "process_task_micro_loan_download_from_khur";
  public static final String ACTIVITY_ID_MICRO_ELEMENTARY_CRITERIA = "PlanItem_0kctick";

  // Mortgage task definition keys
  public static final String TASK_DEF_KEY_MORTGAGE_LOAN_SEND = "user_task_mortgage_loan_send";
  public static final String TASK_DEF_KEY_MORTGAGE_GENERATE_LOAN_DECISION = "user_task_mortgage_generate_loan_decision";

  // New Core definition keys
  public static final String TASK_DEF_KEY_CREATE_IMMOVABLE_COLL = "user_task_new_core_create_immovable_collateral";
  public static final String TASK_DEF_KEY_CREATE_VEHICLE_COLL = "user_task_new_core_create_vehicle_collateral";
  public static final String TASK_DEF_KEY_CREATE_MACHINERY_COLL = "user_task_new_core_create_machinery_collateral";
  public static final String TASK_DEF_KEY_CREATE_OTHER_COLL = "user_task_new_core_create_other_collateral";

  //Branch banking
  // transaction
  public static final String TASK_DEF_KEY_CUSTOMER_TRANSACTION_FORM = "user_task_branch_banking_customer_transaction_form";
  public static final String TASK_DEF_KEY_E_TRANSACTION_FORM = "user_task_branch_banking_e_transaction";

  // Direct Online Salary
  public static final String TASK_DEF_KEY_DIRECT_ONLINE_SET_CONSTANT_VARIABLES = "user_task_set_constant_variables";
  public static final String TASK_DEF_KEY_DIRECT_ONLINE_CLOSE_ACCOUNT_AND_DISBURSE = "user_task_close_account_and_disburse";
  public static final String ACTIVITY_ID_ONLINE_SALARY_MONGOL_BANK = "process_task_download_mongol_bank_info";
  public static final String ACTIVITY_ID_ONLINE_SALARY_ACCEPT_LOAN_AMOUNT = "process_task_calculate_and_accept_loan_amount_process";
  public static final String ACTIVITY_ID_ONLINE_SALARY_CLOSE_AND_DISBURSE = "process_task_close_and_disburse";

  //BNPL
  public static final String TASK_DEF_BNPL_ACCEPT = "user_task_bnpl_accept";

  //Instant Loan
  public static final String TASK_DEF_INSTANT_LOAN_ACCEPT = "user_task_instant_loan_accept";

  //Online leasing
  public static final String TASK_DEF_ONLINE_LEASING_ACCEPT ="user_task_confirm_online_leasing";
}

