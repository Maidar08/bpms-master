import {ColumnDef} from '../../models/common.model';

export const BALANCE_ID_SALE = 'sale';
export const BALANCE_ID_BB_URTUG = 'bbUrtug';

// MICRO BALANCE CALCULATION COLUMNS
export const BALANCE_SALE_COLUMN: ColumnDef[] = [
  {columnDef: BALANCE_ID_SALE, headerText: 'Борлуулалт', type: 'string', edit: true, link: BALANCE_ID_BB_URTUG},
  {columnDef: 'amount1', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent1', headerText: '100%', type: 'number', percent: true},
  {columnDef: BALANCE_ID_BB_URTUG, headerText: 'ББӨртөг', type: 'string'},
  {columnDef: 'amount2', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent2', headerText: '0%', type: 'number', percent: true}
];

export const BALANCE_SALE_TABLE_DATA = [
  { sale: '',  amount1: 0, percent1: 0, bbUrtug: '', amount2: 0, percent2: 0, placeholder: 'Бүтээгдэхүүн'},
  { sale: '',  amount1: 0, percent1: 0, bbUrtug: '', amount2: 0, percent2: 0, placeholder: 'Бүтээгдэхүүн'},
  { sale: '',  amount1: 0, percent1: 0, bbUrtug: '', amount2: 0, percent2: 0, placeholder: 'Бүтээгдэхүүн'},
  ];

export const BALANCE_OPERATION_COLUMN: ColumnDef[] = [
  {columnDef: 'operationCost', headerText: 'Үйл ажиллагааны зардал', type: 'string'},
  {columnDef: 'amount1', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent1', headerText: '0%', type: 'number', percent: true},
  {columnDef: 'operationProfit', headerText: 'Үйл ажиллагааний ашиг', type: 'string'},
  {columnDef: 'amount2', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent2', headerText: '0%', type: 'number', percent: true},
];

export const BALANCE_OPERATION_TABLE_DATA = [
  { operationCost: 'Цалин шимтгэл',  amount1: 0, percent1: 0, operationProfit: 'Үйл ажиллагааны бус орлого', amount2: 0, percent2: 0},
  { operationCost: 'Урсгал зардал',  amount1: 0, percent1: 0, operationProfit: 'Үйл ажиллагааны бус зардал', amount2: 0, percent2: 0},
  { operationCost: 'Түрээсийн зардал',  amount1: 0, percent1: 0, operationProfit: 'Хүүний төлбөр', amount2: 0, percent2: 0},
  { operationCost: 'Дулаан уур усны зардал',  amount1: 0, percent1: 0, operationProfit: 'Татварын өмнөх ашиг', amount2: 0, percent2: 0, disabled: ['amount2', 'percent2']},
  { operationCost: 'Угаалгын зардал',  amount1: 0, percent1: 0, operationProfit: 'Татварын шимтгэл', amount2: 0, percent2: 0},
  { operationCost: 'Тээврийн зардал',  amount1: 0, percent1: 0, operationProfit: 'Цэвэр ашиг', amount2: 0, percent2: 0, disabled: ['amount2', 'percent2']},
  { operationCost: 'Түлш шатахууны зардал',  amount1: 0, percent1: 0, operationProfit: 'Нийт ашиг /EBITDA/', amount2: 0, percent2: 0, disabled: ['amount2', 'percent2']},
  { operationCost: 'Шинжилгээний зардал',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Элэгдэлийн зардал',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Зар сурталчилгаа',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Харилцаа холбоо',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Засвар үйлчилгээ',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Зээл',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Хангамж',  amount1: 0, percent1: 0, operationProfit: ''},
  { operationCost: 'Бусад',  amount1: 0, percent1: 0, operationProfit: ''}
];

export const BALANCE_ASSET_COLUMN: ColumnDef[] = [
  {columnDef: 'currentAssets', headerText: 'Эргэлтийн хөрөнгө', type: 'string'},
  {columnDef: 'amount1', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent1', headerText: '0%', type: 'number', percent: true},
  {columnDef: 'mainAsset', headerText: 'Үндсэн хөрөнгө', type: 'string'},
  {columnDef: 'amount2', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent2', headerText: '0%', type: 'number', percent: true},
];

export const BALANCE_ASSET_TABLE_DATA = [
  { currentAssets: 'Бэлэн мөнгө', amount1: 0, percent1: 0, mainAsset: 'Оффис үйлдвэрлэлийн байр', amount2: 0, percent2: 0 },
  { currentAssets: 'Харилцах хадгаламж', amount1: 0, percent1: 0, mainAsset: 'Орон сууц', amount2: 0, percent2: 0 },
  { currentAssets: 'Авлага', amount1: 0, percent1: 0, mainAsset: 'Хувийн сууц газар', amount2: 0, percent2: 0 },
  { currentAssets: 'Бараа бүтээгдэхүүн', amount1: 0, percent1: 0, mainAsset: 'Тээврийн хэрэгсэл', amount2: 0, percent2: 0 },
  { currentAssets: 'Түүхий эд', amount1: 0, percent1: 0, mainAsset: 'Тоног төхөөрөмж', amount2: 0, percent2: 0 },
  { currentAssets: 'Дуусаагүй үйлдвэрлэл', amount1: 0, percent1: 0, mainAsset: 'Бусад', amount2: 0, percent2: 0 },
  { currentAssets: 'Урьдчилж төлсөн зардал', amount1: 0, percent1: 0, mainAsset: 'Хуримтлагдсан элэгдэл', amount2: 0, percent2: 0 },
  { currentAssets: 'Бусад', amount1: 0, percent1: 0, mainAsset: 'Нийт хөрөнгийн дүн', amount2: 0, percent2: 0, disabled: ['amount2'] },
];

export const BALANCE_DEBT_COLUMN: ColumnDef[] = [
  {columnDef: 'shortTermDebt', headerText: 'Богино хугацааны өр төлбөр', type: 'string'},
  {columnDef: 'amount1', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent1', headerText: '0%', type: 'number', percent: true},
  {columnDef: 'totalDebt', headerText: 'Нийт өр төлбөр', type: 'string'},
  {columnDef: 'amount2', headerText: '0', type: 'number', edit: true, separator: true},
  {columnDef: 'percent2', headerText: '0%', type: 'number', percent: true},
];

export const BALANCE_DEBT_TABLE_DATA = [
  { shortTermDebt: 'Бэлт/нийлүүлэгчийн өглөг', amount1: 0, percent1: 0, totalDebt: 'Эзэмшигчийн өмч', amount2: 0, percent2: 0, disabled: ['amount2', 'percent2'] },
  { shortTermDebt: 'Татварын өглөг', amount1: 0, percent1: 0, totalDebt: 'Өөрийн хөрөнгө', amount2: 0, percent2: 0 },
  { shortTermDebt: 'Бусад өглөг', amount1: 0, percent1: 0, totalDebt: 'Хуримтлагдсан ашиг', amount2: 0, percent2: 0, disabled: ['amount2', 'percent2'] },
  { shortTermDebt: 'Богино хугацааны зээл', amount1: 0, percent1: 0, totalDebt: 'Өнгөрсөн жилийн ашиг', amount2: 0, percent2: 0 },
  { shortTermDebt: 'Урт хугацааны өр төлбөр', amount1: 0, percent1: 0, totalDebt: 'Тухайн жилийн ашиг', amount2: 0, percent2: 0, disabled: ['amount1', 'percent1'] },
  { shortTermDebt: 'Урт хугацааны зээл', amount1: 0, percent1: 0, totalDebt: 'Нийт эх үүсвэр', amount2: 0, percent2: 0, disabled: ['amount2', 'percent2'] },
];
