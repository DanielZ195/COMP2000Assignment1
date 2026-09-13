const {
  Document, Packer, Paragraph, TextRun, HeadingLevel, AlignmentType,
  BorderStyle, LevelFormat, convertInchesToTwip
} = require('docx');
const fs = require('fs');

const ACCENT = '2D5B8E';
const MUTED = '5A6472';

function rule() {
  return new Paragraph({
    text: '',
    spacing: { before: 60, after: 160 },
    border: { bottom: { color: 'C4CCD6', space: 1, style: BorderStyle.SINGLE, size: 6 } },
  });
}

function meta(label, value) {
  return new Paragraph({
    spacing: { after: 60 },
    children: [
      new TextRun({ text: label + '  ', bold: true, size: 20, color: MUTED }),
      new TextRun({ text: value, size: 20 }),
    ],
  });
}

function entryHeading(week, date) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    spacing: { before: 360, after: 80 },
    children: [
      new TextRun({ text: week, bold: true, size: 30, color: ACCENT }),
      new TextRun({ text: '      ' + date, size: 20, color: MUTED }),
    ],
  });
}

function sub(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    spacing: { before: 220, after: 70 },
    children: [new TextRun({ text, bold: true, size: 22, color: '1D2733' })],
  });
}

function body(text) {
  return new Paragraph({
    spacing: { after: 130, line: 300 },
    alignment: AlignmentType.LEFT,
    children: [new TextRun({ text, size: 22 })],
  });
}

function bullet(text) {
  return new Paragraph({
    numbering: { reference: 'dots', level: 0 },
    spacing: { after: 90, line: 290 },
    children: [new TextRun({ text, size: 22 })],
  });
}

const doc = new Document({
  numbering: {
    config: [{
      reference: 'dots',
      levels: [{
        level: 0,
        format: LevelFormat.BULLET,
        text: '•',
        alignment: AlignmentType.LEFT,
        style: { paragraph: { indent: { left: convertInchesToTwip(0.32), hanging: convertInchesToTwip(0.2) } } },
      }],
    }],
  },
  styles: {
    default: {
      document: { run: { font: 'Calibri', size: 22 }, paragraph: { spacing: { line: 300 } } },
    },
  },
  sections: [{
    properties: { page: { margin: { top: 1200, bottom: 1200, left: 1300, right: 1300 } } },
    children: [
      new Paragraph({
        spacing: { after: 40 },
        children: [new TextRun({ text: 'COMP2000 Log Book', bold: true, size: 40, color: '16202B' })],
      }),
      new Paragraph({
        spacing: { after: 220 },
        children: [new TextRun({
          text: 'Object Oriented Programming Practices — Semester Project',
          size: 22, color: MUTED,
        })],
      }),
      meta('Name:', 'Daniel Zohar'),
      meta('Student ID:', '_______________'),
      meta('Project:', 'Predator and prey ecosystem simulation (grid-based, Java/Swing)'),
      meta('Repository:', 'github.com/DanielZ195/COMP2000Assignment1'),
      rule(),

      entryHeading('Week 4', '[DATE OF CLASS]'),
      new Paragraph({
        spacing: { after: 200 },
        children: [new TextRun({
          text: 'Topic: Inheritance and Overloading — designing our class hierarchy',
          italics: true, size: 21, color: MUTED,
        })],
      }),

      sub('What we did'),
      body('This week we worked out how inheritance would actually apply to our simulation rather than just to the textbook examples. We sat down as a group and sketched the classes out on paper, which turned out to be the right call — it was much easier to argue about an arrangement we could all point at than to describe it out loud, and we redrew it several times before we were happy.'),
      body('We concluded that Animal would be the main class, and that each type of animal would be a subclass of it. That way anything common to every animal — its position, and the behaviour every animal shares like moving and feeding — lives in one place, and each species only has to define what makes it different from the others. The alternative we rejected was writing each species as its own independent class, which would have meant copying the same fields and the same movement code into every one of them, and then having to remember to change all of them together whenever a rule changed.'),
      body('We presented our progress to the class during the reporting period.'),

      sub('The hierarchy we designed'),
      bullet('Animal as the superclass, holding the state and behaviour that every animal has in common.'),
      bullet('One subclass per species, each inheriting those common variables and methods.'),
      bullet('Each subclass supplies only what is specific to it, so the shared behaviour is written once.'),

      sub('What I am unsure about'),
      body('The main point of contention we have not resolved is how an object becomes aware of the other objects sharing its cell. There are three options and we could not agree on one: each object keeps track of what is around it, the cell keeps a list of what is in it, or both do and are kept in step with each other.'),
      body('My instinct is that storing it in both places is the one to avoid, because then there are two copies of the same fact and nothing stops them drifting apart — but I am not certain, and it may be that the convenience is worth it. This is the design decision I would most like feedback on.'),

      sub('Reflection'),
      body('What I took from this week is that inheritance is a decision about where to put things, not just a language feature. Deciding that Animal owns the shared state is the same decision as deciding that no species is allowed its own private copy of it, and that constraint is the point rather than a side effect. The question we got stuck on is really the same kind of question one level down — who owns the fact that an animal is standing on a particular cell — and I suspect getting that wrong would be harder to undo later than getting the class hierarchy wrong.'),
    ],
  }],
});

Packer.toBuffer(doc).then((buf) => {
  fs.writeFileSync(process.argv[2], buf);
  console.log('written: ' + process.argv[2] + ' (' + buf.length + ' bytes)');
});
