describe('앱이 잘 실행되는지 확인', () => {
  test('테스트가 정상적으로 동작해야한다.', () => {
    // given
    const initialStatus = true;

    // when
    const expectedStatus = true;

    // then
    expect(initialStatus).toEqual(expectedStatus);
  });
});
