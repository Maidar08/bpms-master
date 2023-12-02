package mn.erin.domain.bpm.usecase.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralId;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.process_parameter.GetProcessParameterByNameAndNameValueAndEntity;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;

import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DATE_FORMAT1;
import static mn.erin.domain.bpm.BpmModuleConstants.DATE_FORMAT2;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.START_DATE;

/**
 * @author Lkhagvadorj
 */

public class GetBpmCollateralByCustNumber extends AbstractUseCase<String, GetCollateralsByCustNumberOutput>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GetBpmCollateralByCustNumber.class);
    private static final String NULL = "null";
    private final ProcessRepository processRepository;

    public GetBpmCollateralByCustNumber(ProcessRepository processRepository)
    {
        this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
    }

    @Override
    public GetCollateralsByCustNumberOutput execute(String cifNumber) throws UseCaseException
    {
        ProcessParameterByNameAndEntityInput processParameterInput = new ProcessParameterByNameAndEntityInput(CIF_NUMBER, cifNumber, ParameterEntityType.COLLATERAL);
        GetProcessParameterByNameAndNameValueAndEntity useCase = new GetProcessParameterByNameAndNameValueAndEntity(processRepository);
        List<Process> processes = useCase.execute(processParameterInput);
        if (processes.isEmpty())
        {
            return new GetCollateralsByCustNumberOutput( Collections.emptyList());
        }
        return new GetCollateralsByCustNumberOutput( mapToCollateralList(processes));
    }

    private List<Collateral> mapToCollateralList(List<Process> processes)
    {
        List<Collateral> collateralList = new ArrayList<>();
        for (Process process : processes )
        {
            Map<ParameterEntityType, Map<String, Serializable>> parameters = process.getProcessParameters();
            if (parameters.isEmpty())
            {
                return Collections.emptyList();
            }

            Map<String, Serializable> collateralParameter = parameters.get(ParameterEntityType.COLLATERAL);
            collateralParameter.forEach( (key, value) -> {
                if (null != value)
                {
                    JSONObject jsonParameter = new JSONObject(value.toString());
                    try
                    {
                        collateralList.add( convertMapToCollateral(jsonParameter) );
                    } catch (ParseException e)
                    {
                        LOGGER.error("############# Could not convert parameter into collateral with key = [{}], value = [{}] ##########", key, value);
                        e.printStackTrace();
                    }
                }
            } );

        }
        return collateralList;
    }
    private Collateral convertMapToCollateral(JSONObject collateralParameter) throws ParseException
    {
        String collateralId = getStringValue( collateralParameter.get(COLLATERAL_ID) );
        String collateralDescription = getStringValue(collateralParameter.get(COLLATERAL_DESCRIPTION));
        BigDecimal collateralAssessment = getBigDecimalValue(collateralParameter.get(COLLATERAL_ASSESSMENT));

        BigDecimal deductionRate = getBigDecimalValue(collateralParameter.get(DEDUCTION_RATE));
        BigDecimal amountOfCollateral = getBigDecimalValue(collateralParameter.get(AMOUNT_OF_COLLATERAL));
        String startDateString = getStringValue(collateralParameter.get(START_DATE));


        CollateralInfo collateralInfo = new CollateralInfo(null, null, deductionRate, collateralAssessment);
        collateralInfo.setDescription(collateralDescription);

        Collateral collateral = new Collateral(CollateralId.valueOf(collateralId), null, null, null);

        if (!StringUtils.isBlank(startDateString))
        {
            try
            {
                collateral.setStartDate( parseDateByFormat(DATE_FORMAT1, startDateString) );
            }
            catch (ParseException e)
            {
                LOGGER.error("############### COULD NOT PARSE DATE IN GET BPM COLLATERAL BY CUST NUMBER wih date = [{}]", startDateString);
                collateral.setStartDate( parseDateByFormat(DATE_FORMAT2, startDateString) );
            }

        }
        collateral.setAmountOfAssessment(amountOfCollateral);
        collateral.setCollateralInfo(collateralInfo);

        return collateral;
    }

    private String getStringValue(Object objectValue)
    {
        if (objectValue.toString().equals(NULL))
        {
            return "";
        }
        return objectValue.toString();
    }

    private BigDecimal getBigDecimalValue(Object objectValue)
    {
        if (objectValue.toString().equals(NULL))
        {
            return new BigDecimal(0);
        }
        return new BigDecimal(objectValue.toString());
    }

    private LocalDate parseDateByFormat(String format, String dateString) throws ParseException
    {
        SimpleDateFormat formatter=new SimpleDateFormat(format);
        Date startDate = formatter.parse(dateString);
        return startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
