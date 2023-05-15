function ConvertDate(date: string) {
  // Create a new Date object with the given string
  var dateObj = new Date(date);

  // Extract the year, month, and day from the date object
  var year = dateObj.getFullYear().toString().substring(2);
  var month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
  var day = dateObj.getDate().toString().padStart(2, '0');

  // Create the formatted date string
  var formattedDate = year + '.' + month + '.' + day;
  return formattedDate;
}

export default ConvertDate;
