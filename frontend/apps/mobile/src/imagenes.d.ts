// Metro convierte cada `import imagen from './imagen.png'` en el identificador numérico del recurso,
// que es lo que espera `<Image source>`.
declare module '*.png' {
  const recurso: number;
  export default recurso;
}
