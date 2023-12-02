import {Injectable} from '@angular/core';
import {fromBuffer} from 'file-type';
import {Document} from '../../models/app.model';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})

export class FileService {
  constructor(private router: Router) {
  }
  public base64ToArrayBuffer(base64) {
    const binaryString = window.atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  public async convert(base64String) {
    const buffer = this.base64ToArrayBuffer(base64String);
    const types = await fromBuffer(buffer);
    return types;
  }

  public download(base64String, mimeType, fileName?) {
    const data = this.base64ToArrayBuffer(base64String);
    const blob = new Blob([data], {type: mimeType});
    if (null == fileName) {
      window.location.href = window.URL.createObjectURL(blob);
    } else {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
    }

  }

  showFileByPlainBase64(documents: Document[], url: string, instanceId: string,
                        isDownloadable: boolean,  rootUrl: string, productCategory): void {
    this.router.navigate([url + 'file-viewer/' + instanceId + '/' + documents[0].name + '/' + isDownloadable],
      {state: {documents, rootUrl, productCategory}})
      .then(() => undefined) ;
  }
}
