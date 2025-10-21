export interface OggettoCarrelloDTO {
  isbn: string;
  nome: string;
  prezzoUnitario: number;
  quantita: number;
}

export interface CarrelloDTO {
  oggetti: OggettoCarrelloDTO[];
  numeroTotaleOggetti: number;
  totale: number;
}