import { fireEvent, render, screen } from '@testing-library/react-native';

import { PantallaInicioSesion } from './PantallaInicioSesion';

async function renderPantalla(props: Partial<Parameters<typeof PantallaInicioSesion>[0]> = {}) {
  await render(
    <PantallaInicioSesion
      onEnviar={jest.fn()}
      onVolver={jest.fn()}
      onIrRegistro={jest.fn()}
      {...props}
    />,
  );
}

describe('PantallaInicioSesion', () => {
  it('muestra los campos de correo y contraseña y el botón de iniciar sesión', async () => {
    await renderPantalla();

    expect(screen.getByLabelText('Correo electrónico')).toBeOnTheScreen();
    expect(screen.getByLabelText('Contraseña')).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Iniciar sesión' })).toBeOnTheScreen();
  });

  it('prepara el campo de correo para escribir un correo', async () => {
    await renderPantalla();

    const correo = screen.getByLabelText('Correo electrónico');

    expect(correo.props.keyboardType).toBe('email-address');
    expect(correo.props.autoCapitalize).toBe('none');
    expect(correo.props.autoComplete).toBe('email');
  });

  it('oculta la contraseña mientras se escribe', async () => {
    await renderPantalla();

    expect(screen.getByLabelText('Contraseña').props.secureTextEntry).toBe(true);
  });

  it('entrega a onEnviar el correo y la contraseña introducidos', async () => {
    const onEnviar = jest.fn();
    await renderPantalla({ onEnviar });

    await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'persona@example.com');

    await fireEvent.changeText(screen.getByLabelText('Contraseña'), 'contraseña-de-prueba');

    await fireEvent.press(screen.getByRole('button', { name: 'Iniciar sesión' }));

    expect(onEnviar).toHaveBeenCalledTimes(1);
    expect(onEnviar).toHaveBeenCalledWith({
      correo: 'persona@example.com',
      contrasena: 'contraseña-de-prueba',
    });
  });

  it('llama a onVolver al pulsar volver', async () => {
    const onVolver = jest.fn();
    await renderPantalla({ onVolver });

    await fireEvent.press(screen.getByRole('button', { name: 'Volver' }));

    expect(onVolver).toHaveBeenCalledTimes(1);
  });

  it('muestra el enlace de registro y navega al pulsarlo', async () => {
    const onIrRegistro = jest.fn();
    await renderPantalla({ onIrRegistro });

    const enlace = screen.getByRole('link', { name: 'Regístrate' });

    expect(enlace).toBeOnTheScreen();

    await fireEvent.press(enlace);

    expect(onIrRegistro).toHaveBeenCalledTimes(1);
  });
});
