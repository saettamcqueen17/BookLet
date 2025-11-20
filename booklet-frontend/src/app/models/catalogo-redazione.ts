
export interface SchedaRedazione {
  isbn: string;
  genere?: string;          // nel backend è "genere", tienilo allineato se serve
  recensione?: string | null;
  valutazioneRedazione?: number | null;
  visibile: boolean;

  titolo : string  ;
  autore : string ;
  immagineLibro : string  ;
  prezzo : bigint ;




}
