function ConvertDate(date: string) {
  // Create a new Date object with the given string
  const dateObj = new Date(date);

  // Extract the year, month, and day from the date object
  const year = dateObj.getFullYear().toString().substring(2);
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0');
  const day = dateObj.getDate().toString().padStart(2, '0');

  // Create the formatted date string
  const formattedDate = year + '.' + month + '.' + day;
  return formattedDate;
}

export default ConvertDate;
