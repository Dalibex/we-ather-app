import { fireEvent, render, screen } from '@testing-library/react-native';

import App from './App';

describe('App', () => {
  it('abre la pantalla de registro desde la pantalla inicial', async () => {
    await render(<App />);

    await fireEvent.press(screen.getByRole('button', { name: 'Crear cuenta' }));

    expect(screen.getByLabelText('Correo electrónico')).toBeOnTheScreen();
  });

  it('vuelve a la pantalla inicial desde la pantalla de registro', async () => {
    await render(<App />);
    await fireEvent.press(screen.getByRole('button', { name: 'Crear cuenta' }));

    await fireEvent.press(screen.getByRole('button', { name: 'Volver' }));

    expect(screen.queryByLabelText('Correo electrónico')).not.toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Crear cuenta' })).toBeOnTheScreen();
  });
});
