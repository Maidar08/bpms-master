/*
 * Copyright (C) ERIN SYSTEMS LLC, 2019. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */
import {MatPaginatorIntl} from '@angular/material/paginator';
import {Injectable} from "@angular/core";

/*This style is for translating the english words in mat paginator to
  mongolian, the total page size and exact location within the range. */

@Injectable()
export class PaginatorIntl extends MatPaginatorIntl {
  itemsPerPageLabel = 'Хуудсанд:';
  nextPageLabel = 'дараах';
  previousPageLabel = 'өмнөх';
  firstPageLabel = 'эхний хуудас';
  lastPageLabel = 'сүүлийн хуудас';

  getRangeLabel = function (page, pageSize, length) {
    if (length === 0 || pageSize === 0) {
      return '1-ээс 1';
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex = startIndex < length ?
      Math.min(startIndex + pageSize, length) :
      startIndex + pageSize;
    return startIndex + 1 + ' - ' + endIndex + ' / ' + 'Нийт: ' + length;
  };
}
