package mn.erin.bpms.loan.consumption.task_listener;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.model.user.UserInfo;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;

public class UserNameTaskListener implements TaskListener
{
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public UserNameTaskListener(AuthenticationService authenticationService, UserRepository userRepository)
    {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Override
    public void notify(DelegateTask delegateTask)
    {
        String caseInstanceId = delegateTask.getCaseInstanceId();
        String currentUserId = authenticationService.getCurrentUserId();

        User currentUser = userRepository.findById(UserId.valueOf(currentUserId));
        UserInfo userInfo = currentUser.getUserInfo();

        String userName = userInfo.getUserName();
        String name;
        if (userName != null) {
            name = userName;
        } else {
            name = currentUserId;
        }

        CaseService caseService = delegateTask.getProcessEngine().getCaseService();

        delegateTask.setVariable("userName", name);
        caseService.setVariable(caseInstanceId, "userName", name);
    }
}
