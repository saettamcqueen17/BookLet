export interface OggettoCarrelloDTO {
  isbn: string;
  prezzoUnitario: number;
  quantita: number;
}

export interface CarrelloDTO {
  oggetti: OggettoCarrelloDTO[];
  numeroTotaleOggetti: number;
  totale: number;
}
