export const FORM_CONSTRAINTS = {
  businessPropertyType: [
    {id: 'own', name: 'Өөрийн эзэмшлийн'},
    {id: 'rental', name: 'Түрээсийн'},
  ],
  industryType: [
    {id: 'trade', name: 'Худалдаа үйлдвэрлэл'},
    {id: 'agriculture', name: 'Хөдөө аж ахуй'},
    {id: 'other', name: 'Бусад'}
  ],
  businessPeriod: [
    {id: 'oneYear', name: '1 жил хүртэл'},
    {id: 'one_twoYear', name: '1-2 жил'},
    {id: 'three_fiveYear', name: '3-5 жил'},
    {id: 'six_sevenYear', name: '6-7 жил'},
    {id: 'moreThanSevenYear', name: '7-с дээш жил'}
  ],
  customerAge: [
    {id: 'until26', name: '26 хүртэл'},
    {id: 'between26_37', name: '26-37'},
    {id: 'between38_47', name: '38-47'},
    {id: 'six_sevenYear', name: '47-с дээш'}
  ],
  generalRemarks: {value: 'Судалгааны талаар ерөнхий тайлбар'}
  ,
  loanReviewComplete: {value: 'Шилжүүлэх'},
  propertyImage: {type: 'File'},
  generateContract: {type: 'File'},
  uploadCustomerContract: {type: 'File'}
};

export const NAV_ITEMS = [
  {icon: 'branch-banking', name: 'Branch Banking', id: 'bpms.bpm.BranchBanking', route: '/loan-page/branch-banking', children: [
    ]},
  {
    icon: 'loan', name: 'Зээлийн хүсэлт', id: undefined, route: '/loan-page/dashboard', children:
      [
        {
          icon: undefined, name: 'Миний зээлийн хүсэлт',
          id: 'bpms.bpm.GetProcessRequestsByAssignedUserId', route: '/loan-page/dashboard/my-loan-request'
        },
        // branch request
        {icon: undefined, name: 'Салбарын зээлийн хүсэлт', id: 'bpms.bpm.GetGroupProcessRequests', route: '/loan-page/dashboard/branch-loan-request'},
        {icon: undefined, name: 'Бүх зээлийн хүсэлт', id: 'bpms.bpm.GetAllProcessRequests', route: '/loan-page/dashboard/all-request'},
        {
          icon: undefined, name: 'Интернет банкны хүсэлт',
          id: 'bpms.bpm.GetUnassignedRequestsByChannel', route: '/loan-page/dashboard/ebank-request'
        },
        // subGroup request
        {icon: undefined, name: 'Салбарын зээлийн хүсэлт',
          id: 'bpms.bpm.GetSubGroupProcessRequests', route: '/loan-page/dashboard/sub-group-request'},
        {icon: undefined, name: 'Харилцагч хайх', id: 'bpms.bpm.SearchByRegisterNumber',
          route: '/loan-page/dashboard/search-customer'}
      ]
  },
  {icon: 'contract', name: 'Зээлийн гэрээ бэлтгэх', id: 'bpms.bpm.LoanContract', route: '/loan-page/loan-contract/all-loan-contract', children: [
      {
        icon: undefined, name: 'Бүх зээлийн гэрээ', id: 'bpms.bpm.GetAllLoanContractRequests', route: '/loan-page/loan-contract/all-loan-contract'
      },
      {
        icon: undefined, name: 'Салбарын зээлийн гэрээ', id: 'bpms.bpm.GetLoanContractProcessRequest', route: '/loan-page/loan-contract/branch-loan-contract'
      },
      {
        icon: undefined, name: 'Салбарын зээлийн гэрээ', id: 'bpms.bpm.GetSubGroupLoanContractProcessRequest', route: '/loan-page/loan-contract/sub-branch-loan-contract'
      }
    ]},
  {icon: 'cog', name: 'Тохиргоо', id: 'bpms.bpm.GroupModuleConfiguration', route: '/loan-page/group', children: []},
  {
    icon: 'organization', name: 'Байгууллагын бүртгэл', id: undefined, route: '/loan-page/dashboard', children:
    [
      {
        icon: undefined, name: 'Цалингийн байгууллага', id: 'bpms.bpm.GetSalaryOrganizationRequests', route: '/loan-page/dashboard/salary-organization-request'
      },
      {
        icon: undefined, name: 'Лизингийн байгууллага', id: 'bpms.bpm.GetLeasingOrganizationRequests', route: '/loan-page/dashboard/leasing-organization-request'
      }
    ]
  }
];
export const INITIAL_STATE = {auth: {permissions: [{permissionId: {id: 'app.service'}}]}};
