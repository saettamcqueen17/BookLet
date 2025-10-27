export interface Libro {
  isbn: string;
  titolo: string;
  autore: string;
  casaEditrice: string;
  descrizione?: string;
  prezzo?: number;
  categoria?: string;
  disponibilita?: number;
  immagineCopertina?: string;
}
