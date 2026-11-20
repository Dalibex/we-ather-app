import { fireEvent, render, screen } from '@testing-library/react-native';
import { AccessibilityInfo, Linking } from 'react-native';

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

  it('muestra el error que recibe del servidor y lo anuncia', async () => {
    const anunciar = jest.spyOn(AccessibilityInfo, 'announceForAccessibility');

    await renderPantalla({ error: 'El correo ya está registrado' });

    expect(screen.getByRole('alert')).toHaveTextContent('El correo ya está registrado');
    expect(anunciar).toHaveBeenCalledWith('El correo ya está registrado');
    anunciar.mockRestore();
  });

  it('mientras se envía no deja volver a pulsar «Crear cuenta»', async () => {
    const onEnviar = jest.fn();
    await renderPantalla({ onEnviar, enviando: true });

    const boton = screen.getByRole('button', { name: 'Crear cuenta' });
    await fireEvent.press(boton);

    expect(boton).toBeDisabled();
    expect(onEnviar).not.toHaveBeenCalled();
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

  describe('validaciones', () => {
    async function rellenar({
      correo = 'persona@example.com',
      contrasena = 'contraseña-de-prueba',
      marcarCasilla = true,
    } = {}) {
      await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), correo);
      await fireEvent.changeText(screen.getByLabelText('Contraseña'), contrasena);
      if (marcarCasilla) await fireEvent.press(screen.getByRole('checkbox', { name: CASILLA }));
    }

    const crearCuenta = () => fireEvent.press(screen.getByRole('button', { name: 'Crear cuenta' }));

    it('no muestra errores antes de pulsar crear cuenta', async () => {
      await renderPantalla();

      await rellenar({ correo: 'persona', contrasena: '123', marcarCasilla: false });

      expect(screen.queryByRole('alert')).not.toBeOnTheScreen();
    });

    it('no llama a onEnviar y muestra todos los errores si el formulario está vacío', async () => {
      const onEnviar = jest.fn();
      await renderPantalla({ onEnviar });

      await crearCuenta();

      expect(onEnviar).not.toHaveBeenCalled();
      expect(screen.getByText('Escribe tu correo electrónico')).toBeOnTheScreen();
      expect(screen.getByText('Escribe una contraseña')).toBeOnTheScreen();
      expect(
        screen.getByText(
          'Para crear la cuenta, confirma que tienes 14 años o más y que has leído la política',
        ),
      ).toBeOnTheScreen();
    });

    it('rechaza un correo con formato incorrecto', async () => {
      const onEnviar = jest.fn();
      await renderPantalla({ onEnviar });

      await rellenar({ correo: 'persona@example' });
      await crearCuenta();

      expect(onEnviar).not.toHaveBeenCalled();
      expect(
        screen.getByText('Escribe un correo válido, como nombre@dominio.com'),
      ).toBeOnTheScreen();
    });

    it('rechaza una contraseña de menos de 8 caracteres', async () => {
      const onEnviar = jest.fn();
      await renderPantalla({ onEnviar });

      await rellenar({ contrasena: '1234567' });
      await crearCuenta();

      expect(onEnviar).not.toHaveBeenCalled();
      expect(screen.getByText('La contraseña debe tener al menos 8 caracteres')).toBeOnTheScreen();
    });

    it('no deja crear la cuenta sin marcar la casilla de privacidad', async () => {
      const onEnviar = jest.fn();
      await renderPantalla({ onEnviar });

      await rellenar({ marcarCasilla: false });
      await crearCuenta();

      expect(onEnviar).not.toHaveBeenCalled();
      expect(
        screen.getByText(
          'Para crear la cuenta, confirma que tienes 14 años o más y que has leído la política',
        ),
      ).toBeOnTheScreen();
    });

    it('anuncia los errores al lector de pantalla', async () => {
      await renderPantalla();

      await crearCuenta();

      expect(screen.getAllByRole('alert')).toHaveLength(3);
    });

    it('borra el error de cada campo en cuanto el usuario lo corrige', async () => {
      await renderPantalla();
      await crearCuenta();

      await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'p');
      expect(screen.queryByText('Escribe tu correo electrónico')).not.toBeOnTheScreen();

      await fireEvent.changeText(screen.getByLabelText('Contraseña'), 'c');
      expect(screen.queryByText('Escribe una contraseña')).not.toBeOnTheScreen();

      await fireEvent.press(screen.getByRole('checkbox', { name: CASILLA }));
      expect(screen.queryByRole('alert')).not.toBeOnTheScreen();
    });

    it('envía el correo sin los espacios del principio ni del final', async () => {
      const onEnviar = jest.fn();
      await renderPantalla({ onEnviar });

      await rellenar({ correo: '  persona@example.com ' });
      await crearCuenta();

      expect(onEnviar).toHaveBeenCalledWith(
        expect.objectContaining({ correo: 'persona@example.com' }),
      );
    });

    it('envía los datos tras corregir los errores', async () => {
      const onEnviar = jest.fn();
      await renderPantalla({ onEnviar });
      await crearCuenta();

      await rellenar();
      await crearCuenta();

      expect(onEnviar).toHaveBeenCalledTimes(1);
      expect(screen.queryByRole('alert')).not.toBeOnTheScreen();
    });
  });

  it('avisa a onCambiarCorreo cada vez que cambia el correo', async () => {
    const onCambiarCorreo = jest.fn();
    await renderPantalla({ onCambiarCorreo });

    await fireEvent.changeText(screen.getByLabelText('Correo electrónico'), 'persona@example.com');

    expect(onCambiarCorreo).toHaveBeenCalledWith('persona@example.com');
  });
});
