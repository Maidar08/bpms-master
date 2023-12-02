export class FileDownloadUtil {
  public static downloadFile(response: any) {
    const blob = new Blob([response.body], {type: response.headers.get('content-type')});
    const objUrl = URL.createObjectURL(blob);
    const downloadLink = document.createElement('a');
    downloadLink.href = objUrl;
    const cd = response.headers.get('content-disposition');
    const filename = cd.match('filename="(.+)"')
    const decodedFileName = decodeURI(filename[1]);
    downloadLink.download = decodedFileName;
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
    URL.revokeObjectURL(objUrl);
  }
}
