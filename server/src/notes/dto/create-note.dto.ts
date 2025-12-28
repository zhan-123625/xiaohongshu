export class CreateNoteDto {
  userId: number;
  title: string;
  content: string;
  type: number;
  coverUrl: string;
  location: string;
  status: number;
}
