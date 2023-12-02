package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

public class SaveXypSalaries extends AuthorizedUseCase<SaveXypSalariesInput, SaveXypSalariesOutput> {
    private static final Permission permission = new BpmModulePermission("SaveSalaries");
    private final ProcessRepository processRepository;

    public SaveXypSalaries(ProcessRepository processRepository, AuthenticationService authenticationService, AuthorizationService authorizationService)
    {
        super(authenticationService, authorizationService);
        this.processRepository = processRepository;
    }

    @Override
    public Permission getPermission() {
        return permission;
    }

    @Override
    protected SaveXypSalariesOutput executeImpl(SaveXypSalariesInput input) throws UseCaseException {
        if (null == input)
        {
            throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
        }
        String processInstanceId = input.getProcessInstanceId();
        List<SalaryInfo> salaries = input.getSalaries();
        Map<String, Serializable> updateValuesMap = new HashMap<>();

        try {
            processRepository.deleteEntity(processInstanceId, ParameterEntityType.XYP_SALARY);
            JSONObject salaryJson = new JSONObject();
            for (SalaryInfo salary : salaries)
            {
                String month = getMonth(String.valueOf(salary.getMonth()));
                String year = String.valueOf(salary.getYear());
                String property = year + "-" + month + "-01";
                BigDecimal amount = salary.getAmount();
                if (amount.doubleValue() > 0)
                {
                    salaryJson.put(property, amount);
                }
            }
            updateValuesMap.put(BpmModuleConstants.XYP_SALAPY, salaryJson.toString());
            int numberUpdated = processRepository.updateParameters(processInstanceId,
                    Collections.singletonMap(ParameterEntityType.XYP_SALARY, updateValuesMap));
            return new SaveXypSalariesOutput(numberUpdated > 0);
        }
        catch (BpmRepositoryException e)
        {
            throw new UseCaseException(e.getMessage());
        }
    }

    private String getMonth(String month)
    {
        if (month.length() == 1)
        {
            return "0" + month;
        }
        return month;
    }
}
