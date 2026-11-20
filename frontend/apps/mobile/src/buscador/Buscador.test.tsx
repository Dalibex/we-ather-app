import { fireEvent, render, screen } from '@testing-library/react-native';

import { Buscador } from './Buscador';

function botonBuscar() {
  return screen.getByRole('button', { name: 'Buscar ruta' });
}

async function escribir(campo: 'Origen' | 'Destino', texto: string) {
  await fireEvent.changeText(screen.getByLabelText(campo), texto);
}

describe('Buscador', () => {
  it('muestra los campos Origen y Destino', async () => {
    await render(<Buscador onBuscar={jest.fn()} />);

    expect(screen.getByLabelText('Origen')).toBeOnTheScreen();
    expect(screen.getByLabelText('Destino')).toBeOnTheScreen();
  });

  it('deja un hueco para las coincidencias debajo de cada campo', async () => {
    await render(<Buscador onBuscar={jest.fn()} />);

    expect(screen.getByTestId('coincidencias-origen')).toBeOnTheScreen();
    expect(screen.getByTestId('coincidencias-destino')).toBeOnTheScreen();
  });

  it('desactiva «Buscar ruta» mientras los dos campos están vacíos', async () => {
    await render(<Buscador onBuscar={jest.fn()} />);

    expect(botonBuscar()).toBeDisabled();
  });

  it.each([
    ['falta el destino', 'Calle Larios', ''],
    ['falta el origen', '', 'Plaza de la Merced'],
    ['el origen solo tiene espacios', '   ', 'Plaza de la Merced'],
  ])('desactiva «Buscar ruta» si %s', async (_caso, origen, destino) => {
    const onBuscar = jest.fn();
    await render(<Buscador onBuscar={onBuscar} />);

    await escribir('Origen', origen);
    await escribir('Destino', destino);
    await fireEvent.press(botonBuscar());

    expect(botonBuscar()).toBeDisabled();
    expect(onBuscar).not.toHaveBeenCalled();
  });

  it('activa «Buscar ruta» con los dos campos rellenos y entrega los textos al pulsarlo', async () => {
    const onBuscar = jest.fn();
    await render(<Buscador onBuscar={onBuscar} />);

    await escribir('Origen', ' Calle Larios ');
    await escribir('Destino', 'Plaza de la Merced');
    expect(botonBuscar()).toBeEnabled();

    await fireEvent.press(botonBuscar());

    expect(onBuscar).toHaveBeenCalledWith({
      origen: 'Calle Larios',
      destino: 'Plaza de la Merced',
    });
  });
});
