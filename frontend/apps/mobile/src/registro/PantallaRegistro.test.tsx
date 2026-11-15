import { fireEvent, render, screen } from '@testing-library/react-native';
import { Linking } from 'react-native';

import { PantallaRegistro } from './PantallaRegistro';

const CASILLA = 'Tengo 14 años o más y he leído la política de privacidad';

function renderPantalla(props: Partial<Parameters<typeof PantallaRegistro>[0]> = {}) {
  return render(
    <PantallaRegistro
      onEnviar={jest.fn()}
      onVolver={jest.fn()}
      onIniciarSesion={jest.fn()}
      {...props}
    />,
  );
}

describe('PantallaRegistro', () => {
  it('muestra los campos de correo y contraseña, la casilla de privacidad y el botón de crear cuenta', async () => {
    await renderPantalla();

    expect(screen.getByLabelText('Correo electrónico')).toBeOnTheScreen();
    expect(screen.getByLabelText('Contraseña')).toBeOnTheScreen();
    expect(screen.getByRole('checkbox', { name: CASILLA })).toBeOnTheScreen();
    expect(screen.getByRole('button', { name: 'Crear cuenta' })).toBeOnTheScreen();
  });

  it('prepara el campo de correo para escribir un correo', async () => {
    await renderPantalla();

    const correo = screen.getByLabelText('Correo electrónico');
    expect(correo.props.keyboardType).toBe('email-address');
    expect(correo.props.autoCapitalize).toBe('none');
  });

  it('oculta la contraseña mientras se escribe', async () => {
    await renderPantalla();

    expect(screen.getByLabelText('Contraseña').props.secureTextEntry).toBe(true);
  });

  it('marca y desmarca la casilla de privacidad al pulsarla', async () => {
    await renderPantalla();
    const casilla = screen.getByRole('checkbox', { name: CASILLA });

    expect(casilla).not.toBeChecked();
    await fireEvent.press(casilla);
    expect(casilla).toBeChecked();
    await fireEvent.press(casilla);
    expect(casilla).not.toBeChecked();
  });

  it('entrega a onEnviar lo que ha escrito el usuario al pulsar crear cuenta', async () => {
    const onEnviar = jest.fn();
    await renderPantalla({ onEnviar });

    await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'persona@example.com');
    await fireEvent.changeText(screen.getByLabelText('Contraseña'), 'contraseña-de-prueba');
    await fireEvent.press(screen.getByRole('checkbox', { name: CASILLA }));
    await fireEvent.press(screen.getByRole('button', { name: 'Crear cuenta' }));

    expect(onEnviar).toHaveBeenCalledTimes(1);
    expect(onEnviar).toHaveBeenCalledWith({
      correo: 'persona@example.com',
      contrasena: 'contraseña-de-prueba',
      confirmaEdadYPolitica: true,
    });
  });

  it('llama a onVolver al pulsar volver', async () => {
    const onVolver = jest.fn();
    await renderPantalla({ onVolver });

    await fireEvent.press(screen.getByRole('button', { name: 'Volver' }));

    expect(onVolver).toHaveBeenCalledTimes(1);
  });

  it('abre la política de privacidad del repositorio al pulsar el enlace', async () => {
    const openURL = jest.spyOn(Linking, 'openURL').mockResolvedValue(undefined);
    await renderPantalla();

    await fireEvent.press(screen.getByRole('link', { name: 'Leer la política de privacidad' }));

    expect(openURL).toHaveBeenCalledWith(
      'https://github.com/Dalibex/we-ather-app/blob/main/docs/privacidad.md',
    );
  });

  it('lleva al inicio de sesión al pulsar el enlace para quien ya tiene cuenta', async () => {
    const onIniciarSesion = jest.fn();
    await renderPantalla({ onIniciarSesion });

    await fireEvent.press(screen.getByRole('link', { name: 'Inicia sesión' }));

    expect(onIniciarSesion).toHaveBeenCalledTimes(1);
  });

  it('mantiene el nombre de la casilla al marcarla, para que el lector de pantalla diga lo mismo', async () => {
    await renderPantalla();

    await fireEvent.press(screen.getByRole('checkbox', { name: CASILLA }));

    expect(screen.getByRole('checkbox', { name: CASILLA })).toBeChecked();
  });

  it('empieza con el correo que recibe en correoInicial', async () => {
    await renderPantalla({ correoInicial: 'persona@example.com' });

    expect(screen.getByLabelText('Correo electrónico').props.value).toBe('persona@example.com');
  });

  it('avisa a onCambiarCorreo cada vez que cambia el correo', async () => {
    const onCambiarCorreo = jest.fn();
    await renderPantalla({ onCambiarCorreo });

    await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'persona@example.com');

    expect(onCambiarCorreo).toHaveBeenCalledWith('persona@example.com');
  });
});
