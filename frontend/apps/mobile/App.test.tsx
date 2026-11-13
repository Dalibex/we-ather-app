import { render, screen } from '@testing-library/react-native';

import App from './App';

describe('App', () => {
  it('muestra el mapa base como pantalla principal', async () => {
    await render(<App />);

    expect(screen.getByTestId('mapa-base')).toBeOnTheScreen();
  });
});
