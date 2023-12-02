export class DateFormatter {
  public static dateFormat(date: Date): string {
    return date.getFullYear() + '/' + this.zeroDigitPlace(date.getMonth() + 1) + '/' + this.zeroDigitPlace(date.getDate());
  }

  public static timeFormat(date: Date): string {
    return this.zeroDigitPlace(date.getHours()) + ':' + this.zeroDigitPlace(date.getMinutes()) + ':' + this.zeroDigitPlace(date.getSeconds());
  }

  private static zeroDigitPlace(number: number): string {
    return (number < 10) ? '0' + number : number.toString();
  }
}
