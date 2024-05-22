function loader(mode) {
  const $loader = $('#loader');
  switch (mode) {
    case 0:
      $loader.addClass('hidden');
      break;
    case 1:
      $loader.removeClass('hidden');
      break;
  }
}
