import { Pipe, PipeTransform } from '@angular/core';
import { NodeAttrbute, NodeEntity } from './node.types';

/**
 * Safe pipe for casting an AbstractControl to a FormArray.
 * Guarantees that the control is a FormArray (not just an AbstractControl).
 *
 * @example
 * <alv-some-input-field [someFormArray]="controlThatShouldBeFormArray | asFormArray">
 *
 * @throws Error if the control is <i>not a FormArray</i>
 */
@Pipe({
  name: 'nodeAttributes',
  standalone: true
})
export class NodeAttributesPipe implements PipeTransform {

  transform(node: NodeEntity | null, ignore: string[] = []): NodeAttrbute[] {
    if (!node) {
      return [];
    }
    return Object.keys(node)
      .filter(key => key !== 'id' && key !== 'dtype' && key !== 'childNodes')
      .filter(key => !ignore?.includes(key))
      .map(key => {
        // @ts-ignore
        const value = node[key] || '';
        const type = key.includes('Date') ? 'date'
          : key.includes('yesNo') ? 'boolean'
            : typeof value;
        return ({ name: key, value, type });
      });
  }

}
